package com.dream11.rest;

import com.dream11.rest.Constants;
import com.dream11.rest.Setup;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@ExtendWith({VertxExtension.class, Setup.class})
@Slf4j
class RestApiIT {

  private final CloseableHttpClient httpClient = HttpClients.createDefault();

  @Test
  void nullHeaderTest() throws IOException {
    String uri = String.format("http://127.0.0.1:%s%s/1?testFilter=query&double=1.1&float=1.1&integer=1&long=1",
        Constants.APPLICATION_PORT, Constants.VALIDATION_ROUTE_PATH);
    HttpResponse response = httpClient.execute(new HttpGet(uri));
    MatcherAssert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(400));
  }

  @Test
  void nullQueryParamTest() throws IOException {
    String uri = String.format("http://127.0.0.1:%s%s/1", Constants.APPLICATION_PORT, Constants.VALIDATION_ROUTE_PATH);
    HttpGet request = new HttpGet(uri);
    request.setHeader("testHeader", "1");
    HttpResponse response = httpClient.execute(request);
    MatcherAssert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(400));
  }

  @ParameterizedTest
  @CsvSource({"/class", "/method"})
  void timeoutAnnotationTest(String path) throws IOException {
    String uri = String.format("http://127.0.0.1:%s%s%s", Constants.APPLICATION_PORT, Constants.TIMEOUT_ROUTE_PATH, path);
    HttpResponse response = httpClient.execute(new HttpGet(uri));
    MatcherAssert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(503));
  }

  @ParameterizedTest
  @CsvSource({"integer", "long", "float", "double"})
  void typeTypeValidationParamTest(String param) throws IOException {
    String uri =
        String.format("http://127.0.0.1:%s%s/1?testFilter=query&%s=param", Constants.APPLICATION_PORT, Constants.VALIDATION_ROUTE_PATH,
            param);
    HttpGet request = new HttpGet(uri);
    request.setHeader("testHeader", "1");
    HttpResponse response = httpClient.execute(request);
    JsonObject responseBody = new JsonObject(IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8));
    MatcherAssert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(400));
    MatcherAssert.assertThat(responseBody.getJsonObject("error").getString("code"), Matchers.equalTo("BAD_REQUEST"));
    MatcherAssert.assertThat(responseBody.getJsonObject("error").getString("message"),
        Matchers.equalTo(String.format("Query param '%s' must be %s", param, param)));
  }

  @Test
  void validateBodyTest() throws IOException {
    String uri = String.format("http://127.0.0.1:%s%s", Constants.APPLICATION_PORT, Constants.VALIDATION_ROUTE_PATH);
    HttpPost request = new HttpPost(uri);
    request.setHeader("Content-type", "application/json");
    JsonObject json = new JsonObject().put("resourceId", "Hello");
    request.setEntity(new StringEntity(json.toString()));
    HttpResponse response = httpClient.execute(request);
    JsonObject responseBody = new JsonObject(IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8));
    MatcherAssert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(400));
    MatcherAssert.assertThat(responseBody.getJsonObject("error").getString("code"), Matchers.equalTo("BAD_REQUEST"));
    MatcherAssert.assertThat(responseBody.getJsonObject("error").getString("message"), Matchers.equalTo("resourceId must be integer"));
  }

  @Test
  void positiveBodyTest() throws IOException {
    String uri = String.format("http://127.0.0.1:%s%s", Constants.APPLICATION_PORT, Constants.VALIDATION_ROUTE_PATH);
    HttpPost request = new HttpPost(uri);
    request.setHeader("Content-type", "application/json");
    JsonObject json = new JsonObject().put("resourceId", "1");
    request.setEntity(new StringEntity(json.toString()));
    HttpResponse response = httpClient.execute(request);
    MatcherAssert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(200));
  }

  @Test
  void routeNotFoundTest() throws IOException {
    String uri = String.format("http://127.0.0.1:%s%s", Constants.APPLICATION_PORT, "/nonexistent");
    HttpResponse response = httpClient.execute(new HttpGet(uri));
    MatcherAssert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(404));
  }

}
