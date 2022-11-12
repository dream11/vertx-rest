package com.dream11.rest.app.integration;

import com.dream11.rest.app.Constants;
import com.dream11.rest.app.Setup;
import com.dream11.rest.app.inject.AppContext;
import com.dream11.rest.app.module.MainModule;
import com.dream11.rest.app.verticle.RestVerticle;
import io.reactivex.plugins.RxJavaPlugins;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@ExtendWith({VertxExtension.class, Setup.class})
@Slf4j
public class RestAppIT {

    static HttpClient httpClient;
    static Vertx vertx = Vertx.vertx();;

    @BeforeAll
    public static void setup() {
        AppContext.initialize(Arrays.asList(new MainModule[]{new MainModule(vertx)}));
    }
    @Test
    public void healthCheckTest(VertxTestContext testContext) {
        final String verticleName = RestVerticle.class.getName();
        vertx.rxDeployVerticle(AppContext.getContextInstance().getInstance(RestVerticle.class))
                .doOnError(error -> log.error("Error in deploying verticle : {}", verticleName, error))
                .doOnSuccess(deploymentId -> {
                    log.info("Deployed verticle : {}", verticleName);
                    httpClient = HttpClientBuilder.create().build();
                    HttpResponse response = httpClient.execute(new HttpGet("http://127.0.0.1:80/healthcheck"));
                    MatcherAssert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(200));
                    MatcherAssert.assertThat(IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8),
                            Matchers.equalTo(Constants.HEALTHCHECK_RESPONSE));
                })
                .subscribe(deploymentId -> testContext.completeNow(), testContext::failNow);
    }

}
