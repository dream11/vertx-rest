package com.dream11.rest;

import com.dream11.rest.injector.GuiceInjector;
import com.dream11.rest.util.SharedDataUtil;
import com.dream11.rest.verticle.RestVerticle;
import com.google.inject.Guice;
import io.vertx.core.DeploymentOptions;
import io.vertx.rxjava3.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

@Slf4j
public class Setup implements BeforeAllCallback, AfterAllCallback, ExtensionContext.Store.CloseableResource {
  final Vertx vertx = Vertx.vertx();

  @Override
  public void afterAll(ExtensionContext extensionContext) {
    this.vertx.rxClose().blockingAwait();
  }

  @Override
  public void beforeAll(ExtensionContext extensionContext) {
    GuiceInjector injector = new GuiceInjector(Guice.createInjector());
    SharedDataUtil.setInstance(vertx.getDelegate(), injector);
    final String verticleName = RestVerticle.class.getName();
    System.setProperty("rx3.buffer-size", "1"); // Set rxjava buffer size
    String __ = this.vertx.rxDeployVerticle(injector.getInstance(RestVerticle.class), new DeploymentOptions().setInstances(1))
        .doOnError(error -> log.error("Error in deploying verticle : {}", verticleName, error))
        .doOnSuccess(v -> log.info("Deployed verticle : {}", verticleName)).blockingGet();
  }

  @Override
  public void close() {

  }
}
