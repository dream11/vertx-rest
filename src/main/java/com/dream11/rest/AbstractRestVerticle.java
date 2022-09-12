package com.dream11.rest;

import com.dream11.rest.exception.mapper.GenericExceptionMapper;
import com.dream11.rest.exception.mapper.ValidationExceptionMapper;
import com.dream11.rest.exception.mapper.WebApplicationExceptionMapper;
import com.dream11.rest.filter.LoggerFilter;
import com.dream11.rest.filter.RequestResponseFilter;
import com.dream11.rest.filter.TimeoutFilter;
import com.dream11.rest.provider.JsonProvider;
import com.dream11.rest.provider.ParamConverterProvider;
import com.dream11.rest.route.ClassInjector;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.ResponseContentTypeHandler;
import io.vertx.reactivex.ext.web.handler.StaticHandler;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jboss.resteasy.plugins.server.vertx.VertxRequestHandler;
import org.jboss.resteasy.plugins.server.vertx.VertxResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class AbstractRestVerticle extends AbstractVerticle {

    private final String packageName;
    private final HttpServerOptions httpServerOptions;
    private final ClassInjector injector;
    private HttpServer httpServer;

    public AbstractRestVerticle(String packageName, ClassInjector injector) {
        this(packageName, new HttpServerOptions(), injector);
    }

    public AbstractRestVerticle(String packageName, HttpServerOptions httpServerOptions, ClassInjector injector) {
        this.packageName = packageName;
        this.httpServerOptions = httpServerOptions;
        this.injector = injector;
    }

    protected RequestResponseFilter getReqResFilter() {
        return new LoggerFilter();
    }

    @Override
    public Completable rxStart() {
        return startHttpServer().doOnSuccess(server -> {
            this.httpServer = server;
        }).ignoreElement();
    }

    private Single<HttpServer> startHttpServer() {
        VertxResteasyDeployment deployment = getResteasyDeployment();
        Router router = getRouter();
        VertxRequestHandler vertxRequestHandler = new VertxRequestHandler(vertx.getDelegate(), deployment);
        val server = vertx.createHttpServer(this.httpServerOptions);
        val handleRequests = server.requestStream()
                .toFlowable()
                .map(HttpServerRequest::pause)
                .onBackpressureDrop(req -> {
                    log.error("Dropping request with status 503");
                    req.response().setStatusCode(503).end();
                })
                // TODO: Understand why removing this seemingly redundant observeOn increases latency & CPU
                .observeOn(RxHelper.scheduler(new io.vertx.reactivex.core.Context(this.context)))
                .doOnNext(req -> {
                    if (req.path().matches("/swagger(.*)")) {
                        router.handle(req);
                    } else {
                        vertxRequestHandler.handle(req.getDelegate());
                    }
                })
                .map(HttpServerRequest::resume)
                .doOnError(error -> log.error("Uncaught ERROR while handling request", error))
                .ignoreElements();

        return server
                .rxListen()
                .doOnSuccess(res -> log.info("Started http server at " + this.httpServerOptions.getPort() + " package : " + packageName))
                .doOnError(error -> log.error(
                        "Failed to start http server at port :" + this.httpServerOptions.getPort() + " with error " + error.getMessage()))
                .doOnSubscribe(disposable -> handleRequests.subscribe());
    }

    protected Router getRouter() {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(ResponseContentTypeHandler.create());
        router.route().handler(StaticHandler.create());
        return router;
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        if (this.httpServer != null) {
            log.info("stopping http server.");
            this.httpServer.close();
        }
        super.stop(stopPromise);
    }

    protected VertxResteasyDeployment getResteasyDeployment() {
        VertxResteasyDeployment deployment = new VertxResteasyDeployment();
        deployment.start();
        List<Class<?>> routes = RestUtil.annotatedClasses(packageName, Path.class);
        log.info("JAX-RS routes : " + routes.size());
        ResteasyProviderFactory resteasyProviderFactory = deployment.getProviderFactory();
        getProviders().forEach(resteasyProviderFactory::register);
        resteasyProviderFactory.register(getReqResFilter());
        // not using deployment.getRegistry().addPerInstanceResource because it creates new instance of resource for each request
        routes.forEach(route -> {
            deployment.getRegistry().addSingletonResource(injector.getInstance(route));
        });
        return deployment;
    }

    protected List<Class<?>> getProviders() {
        List<Class<?>> providers = new ArrayList<>();
        providers.add(TimeoutFilter.class);
        providers.add(ValidationExceptionMapper.class);
        providers.add(GenericExceptionMapper.class);
        providers.add(WebApplicationExceptionMapper.class);
        providers.add(JsonProvider.class);
        providers.add(ParamConverterProvider.class);
        providers.addAll(RestUtil.annotatedClasses(packageName, Provider.class));
        return providers;
    }
}
