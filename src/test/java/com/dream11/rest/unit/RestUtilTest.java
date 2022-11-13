package com.dream11.rest.unit;

import com.dream11.rest.app.Constants;
import com.dream11.rest.app.routes.HealthCheckRoute;
import com.dream11.rest.app.routes.TimeoutRoute;
import com.dream11.rest.app.routes.ValidationCheckRoute;
import com.dream11.rest.util.RestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.vertx.core.json.JsonObject;
import jakarta.ws.rs.Path;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

class RestUtilTest {
  @Test
  void testAnnotatedClasses() {
    List<Class<?>> classes = RestUtil.annotatedClasses(Constants.TEST_PACKAGE_NAME, Path.class);
    MatcherAssert.assertThat(classes.size(), Matchers.equalTo(3));
    MatcherAssert.assertThat(classes, Matchers.containsInAnyOrder(ValidationCheckRoute.class, HealthCheckRoute.class, TimeoutRoute.class));
  }

  @Test
  void testGetString() throws JsonProcessingException {
    JsonObject healthCheckJson = new JsonObject(Constants.HEALTHCHECK_RESPONSE);
    MatcherAssert.assertThat(RestUtil.objectToString(Constants.HEALTHCHECK_RESPONSE), Matchers.equalTo(Constants.HEALTHCHECK_RESPONSE));
    MatcherAssert.assertThat(RestUtil.objectToString(healthCheckJson), Matchers.equalTo(Constants.HEALTHCHECK_RESPONSE));
    MatcherAssert.assertThat(RestUtil.objectToString(healthCheckJson.getJsonArray("checks").getList()),
        Matchers.equalTo(healthCheckJson.getValue("checks").toString()));
  }

  @Test
  void testListObjectToString() throws JsonProcessingException {
    List<Integer> integerList = Arrays.asList(1, 2, 3);
    MatcherAssert.assertThat(RestUtil.objectToString(integerList), Matchers.equalTo("[1,2,3]"));
  }

  @Test
  void testStringObjectToString() throws JsonProcessingException {
    String message = "MESSAGE";
    MatcherAssert.assertThat(RestUtil.objectToString(message), Matchers.equalTo(message));
  }

  @Test
  void testJsonObjectToString() throws JsonProcessingException {
    JsonObject jsonObject = new JsonObject().put("key", "key");
    MatcherAssert.assertThat(RestUtil.objectToString(jsonObject), Matchers.equalTo(jsonObject.toString()));
  }
}
