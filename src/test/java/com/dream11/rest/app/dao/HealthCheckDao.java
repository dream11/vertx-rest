package com.dream11.rest.app.dao;

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
                .map(isConnected -> new JsonObject().put("isConnected", true));
    }
}
