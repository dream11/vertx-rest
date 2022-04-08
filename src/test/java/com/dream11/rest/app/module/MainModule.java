package com.dream11.rest.app.module;

import com.google.inject.AbstractModule;
import io.d11.reactivex.aerospike.client.AerospikeClient;
import io.vertx.reactivex.core.Vertx;

public class MainModule extends AbstractModule {
    final Vertx vertx;

    public MainModule(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    protected void configure() {
        bind(AerospikeClient.class).toProvider(()->vertx.getOrCreateContext().get(AerospikeClient.class.getName()));
    }
}
