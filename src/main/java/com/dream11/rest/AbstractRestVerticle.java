package com.dream11.rest;

import com.dream11.rest.exception.mapper.GenericExceptionMapper;
import com.dream11.rest.exception.mapper.ValidationExceptionMapper;
import com.dream11.rest.exception.mapper.WebApplicationExceptionMapper;
import com.dream11.rest.filter.LoggerFilter;
import com.dream11.rest.filter.RequestResponseFilter;
import com.dream11.rest.filter.TimeoutFilter;
import com.dream11.rest.provider.JsonProvider;
import com.dream11.rest.provider.ParamConverterProvider;
import com.dream11.rest.util.AnnotationUtil;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.Context;
import io.vertx.rxjava3.core.RxHelper;
import io.vertx.rxjava3.core.http.HttpServer;
import io.vertx.rxjava3.core.http.HttpServerRequest;
import io.vertx.rxjava3.ext.web.Router;
import io.vertx.rxjava3.ext.web.handler.BodyHandler;
import io.vertx.rxjava3.ext.web.handler.ResponseContentTypeHandler;
import io.vertx.rxjava3.ext.web.handler.StaticHandler;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jboss.resteasy.plugins.server.vertx.VertxRequestHandler;
import org.jboss.resteasy.plugins.server.vertx.VertxResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * Starts backpressure enabled HTTP server and registers providers in resteasy deployment.
 */
@Slf4j
public abstract class AbstractRestVerticle extends AbstractVerticle {

  final String packageName;
  final HttpServerOptions httpServerOptions;
  HttpServer httpServer;

  protected AbstractRestVerticle(String packageName) {
    this(packageName, new HttpServerOptions());
  }

  protected AbstractRestVerticle(String packageName, HttpServerOptions httpServerOptions) {
    this.packageName = packageName;
    this.httpServerOptions = httpServerOptions;
  }

  protected abstract ClassInjector getInjector();

  protected RequestResponseFilter getReqResFilter() {
    return new LoggerFilter();
  }

  @Override
  public Completable rxStart() {
    return this.startHttpServer().doOnSuccess(server -> this.httpServer = server).ignoreElement();
  }

  private Single<HttpServer> startHttpServer() {
    VertxResteasyDeployment deployment = this.buildResteasyDeployment();
    Router router = this.getRouter();
    VertxRequestHandler vertxRequestHandler = new VertxRequestHandler(vertx.getDelegate(), deployment);
    val server = vertx.createHttpServer(this.httpServerOptions);
    val handleRequests = server.requestStream()
        .toFlowable()
        .map(HttpServerRequest::pause)
        .onBackpressureDrop(req -> {
          log.error("Dropping request with status 503");
          req.getDelegate().response().setStatusCode(503).end();
        })
        .observeOn(RxHelper.scheduler(new Context(this.context))) // For backpressure
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
        .doOnSuccess(res -> log.info("Started http server at port: {} for package: {}", this.httpServerOptions.getPort(), packageName))
        .doOnError(error -> log.error(
            "Failed to start http server at port : {} with error: {}", this.httpServerOptions.getPort(), error.getMessage()))
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
  public Completable rxStop() {
    if (this.httpServer != null) {
      return this.httpServer.rxClose()
          .doOnComplete(() -> log.info("http server stopped successfully"))
          .doOnError(err -> log.info("Failed to stop http server", err));
    }
    return Completable.complete();
  }

  protected VertxResteasyDeployment buildResteasyDeployment() {
    VertxResteasyDeployment deployment = new VertxResteasyDeployment();
    deployment.start();
    List<Class<?>> routes = AnnotationUtil.getClassesWithAnnotation(packageName, Path.class);
    log.info("JAX-RS routes : " + routes.size());
    ResteasyProviderFactory resteasyProviderFactory = deployment.getProviderFactory();
    this.getProviders().forEach(resteasyProviderFactory::register);
    // not using deployment.getRegistry().addPerInstanceResource because it creates new instance of resource for each request
    routes.forEach(route -> deployment.getRegistry().addSingletonResource(this.getInjector().getInstance(route)));
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
    providers.add(this.getReqResFilter().getClass());
    providers.addAll(AnnotationUtil.getClassesWithAnnotation(packageName, Provider.class));
    return providers;
  }
}
