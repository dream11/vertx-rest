package com.dream11.rest.unit;

import com.dream11.rest.RestUtil;
import com.dream11.rest.app.Constants;
import com.dream11.rest.app.routes.HealthCheckRoute;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.vertx.core.json.JsonObject;
import jakarta.ws.rs.Path;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RestUtilTest {
    @Test
    public void testAnnotatedClasses() {
        List<Class<?>> classes = RestUtil.annotatedClasses(Constants.TEST_PACKAGE_NAME, Path.class);
        MatcherAssert.assertThat(classes.size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(classes, Matchers.contains(HealthCheckRoute.class));
    }

    @Test
    public void testGetString() throws JsonProcessingException {
        JsonObject healthCheckJson = new JsonObject(Constants.HEALTHCHECK_RESPONSE);
        MatcherAssert.assertThat(RestUtil.getString(Constants.HEALTHCHECK_RESPONSE), Matchers.equalTo(Constants.HEALTHCHECK_RESPONSE));
        MatcherAssert.assertThat(RestUtil.getString(healthCheckJson), Matchers.equalTo(Constants.HEALTHCHECK_RESPONSE));
        MatcherAssert.assertThat(RestUtil.getString(healthCheckJson.getJsonArray("checks").getList()),
                Matchers.equalTo(healthCheckJson.getValue("checks").toString()));
    }
}
