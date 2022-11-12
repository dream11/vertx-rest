package com.dream11.rest.app;

public class Constants {
  public static final String NAMESPACE = "NAMESPACE";
  public static final String TEST_NAMESPACE = "test";
  public static final String AEROSPIKE_HOST = "aerospike.host";
  public static final String AEROSPIKE_PORT = "aerospike.port";
  public static final String AEROSPIKE_IMAGE_KEY = "aerospike.image";
  public static final String DEFAULT_AEROSPIKE_IMAGE = "aerospike/aerospike-server";
  public static final String HEALTHCHECK_RESPONSE =
      "{\"checks\":[{\"error\":null,\"response\":{\"isConnected\":true},\"status\":\"UP\",\"type\":\"aerospike\"}]}";
  public static final String TEST_PACKAGE_NAME = "com.dream11.rest";

  public static final String HEALTHCHECK_ROUTE_PATH = "/healthcheck";
  public static final String VALIDATION_ROUTE_PATH = "/validation";
  public static final String TIMEOUT_ROUTE_PATH = "/timeout";

  public static final Integer APPLICATION_PORT = 8080;

}
