package com.dream11.rest.app;

public class Constants {
    public static final String NAMESPACE = "NAMESPACE";
    public static final String TEST_NAMESPACE = "test";
    public static final String TEST_SET = "testset";
    public static final String AEROSPIKE_HOST = "aerospike.host";
    public static final String AEROSPIKE_PORT = "aerospike.port";
    public static final String AEROSPIKE_IMAGE_KEY = "aerospike.image";
    public static final String DEFAULT_AEROSPIKE_IMAGE = "aerospike/aerospike-server";
    public static final String INIT_DATA_PATH = "src/test/resources/init.aql";
    public static final String INIT_DATA_PATH_IN_CONTAINER = "/aerospike-seed/init.aql";
    public static final String HEALTHCHECK_RESPONSE = "{\"checks\":[{\"error\":null,\"response\":{\"isConnected\":true},\"status\":\"UP\",\"type\":\"aerospike\"}]}";
    public static final String TEST_PACKAGE_NAME = "com.dream11.rest";
}
