package com.dream11.rest.app.dao;

import com.dream11.rest.app.error.RestAppError;
import com.dream11.rest.exception.RestError;
import com.dream11.rest.exception.RestException;
import com.google.inject.Inject;
import io.d11.reactivex.aerospike.client.AerospikeClient;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;

public class HealthCheckDao {
    @Inject
    AerospikeClient client;

    public Single<JsonObject> aerospikeHealthCheck() {
        return client
                .rxIsConnected()
                .flatMap(isConnected -> isConnected ?
                        Single.just(new JsonObject().put("isConnected", true)) :
                        Single.error(new RestException(RestError.of(RestAppError.AEROSPIKE_NOT_CONNECTED))));
    }
}
