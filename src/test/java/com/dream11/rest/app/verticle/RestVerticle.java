package com.dream11.rest.app.verticle;

import com.dream11.rest.AbstractRestVerticle;
import com.dream11.rest.app.Constants;
import com.dream11.rest.app.inject.AppContext;
import io.d11.aerospike.client.AerospikeConnectOptions;
import io.d11.reactivex.aerospike.client.AerospikeClient;
import io.reactivex.Completable;
import lombok.experimental.NonFinal;

public class RestVerticle extends AbstractRestVerticle {
    @NonFinal
    private static AerospikeClient aerospikeClient;

    public RestVerticle() {
        super(Constants.TEST_PACKAGE_NAME, AppContext.getContextInstance());
    }

    @Override
    public Completable rxStart() {
        AerospikeConnectOptions connectOptions = new AerospikeConnectOptions()
                .setHost(System.getProperty(Constants.AEROSPIKE_HOST))
                .setPort(Integer.parseInt(System.getProperty(Constants.AEROSPIKE_PORT)));
        aerospikeClient = AerospikeClient.create(vertx, connectOptions);
        vertx.getOrCreateContext().put(AerospikeClient.class.getName(), aerospikeClient);
        return super.rxStart();
    }

    @Override
    public Completable rxStop() {
        aerospikeClient.getDelegate().close();
        return super.rxStop();
    }
}
