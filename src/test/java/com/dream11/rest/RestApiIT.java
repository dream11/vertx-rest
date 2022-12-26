package com.dream11.rest;

import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith({VertxExtension.class, Setup.class})
@Slf4j
class RestApiIT {

  final CloseableHttpClient httpClient = HttpClients.createDefault();

  @Test
  void nullHeaderTest() throws IOException {
    // arrange
    String uri = String.format("http://127.0.0.1:%s%s/1?testFilter=query&double=1.1&float=1.1&integer=1&long=1",
        Constants.APPLICATION_PORT, Constants.VALIDATION_ROUTE_PATH);
    // act
    HttpResponse response = httpClient.execute(new HttpGet(uri));
    // assert
    assertThat(response.getStatusLine().getStatusCode(), equalTo(400));
  }

  @Test
  void nullQueryParamTest() throws IOException {
    // arrange
    String uri = String.format("http://127.0.0.1:%s%s/1", Constants.APPLICATION_PORT, Constants.VALIDATION_ROUTE_PATH);
    HttpGet request = new HttpGet(uri);
    request.setHeader("testHeader", "1");
    // act
    HttpResponse response = httpClient.execute(request);
    // assert
    assertThat(response.getStatusLine().getStatusCode(), equalTo(400));
  }

  @ParameterizedTest
  @CsvSource({"/class", "/method"})
  void timeoutAnnotationTest(String path) throws IOException {
    // arrange
    String uri = String.format("http://127.0.0.1:%s%s%s", Constants.APPLICATION_PORT, Constants.TIMEOUT_ROUTE_PATH, path);
    // act
    HttpResponse response = httpClient.execute(new HttpGet(uri));
    // assert
    assertThat(response.getStatusLine().getStatusCode(), equalTo(503));
  }

  @ParameterizedTest
  @CsvSource({"integer", "long", "float", "double"})
  void typeTypeValidationParamTest(String param) throws IOException {
    // arrange
    String uri =
        String.format("http://127.0.0.1:%s%s/1?testFilter=query&%s=param", Constants.APPLICATION_PORT, Constants.VALIDATION_ROUTE_PATH,
            param);
    HttpGet request = new HttpGet(uri);
    request.setHeader("testHeader", "1");
    // act
    HttpResponse response = httpClient.execute(request);
    JsonObject responseBody = new JsonObject(IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8));
    // assert
    assertThat(response.getStatusLine().getStatusCode(), equalTo(400));
    assertThat(responseBody.getJsonObject("error").getString("code"), equalTo("BAD_REQUEST"));
    assertThat(responseBody.getJsonObject("error").getString("message"),
        equalTo(String.format("Query param '%s' must be %s", param, param)));
  }

  @Test
  void validateBodyTest() throws IOException {
    // arrange
    String uri = String.format("http://127.0.0.1:%s%s", Constants.APPLICATION_PORT, Constants.VALIDATION_ROUTE_PATH);
    HttpPost request = new HttpPost(uri);
    request.setHeader("Content-type", "application/json");
    JsonObject json = new JsonObject().put("resourceId", "Hello");
    request.setEntity(new StringEntity(json.toString()));
    // act
    HttpResponse response = httpClient.execute(request);
    JsonObject responseBody = new JsonObject(IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8));
    // assert
    assertThat(response.getStatusLine().getStatusCode(), equalTo(400));
    assertThat(responseBody.getJsonObject("error").getString("code"), equalTo("BAD_REQUEST"));
    assertThat(responseBody.getJsonObject("error").getString("message"), equalTo("resourceId must be integer"));
  }

  @Test
  void positiveBodyTest() throws IOException {
    // arrange
    String uri = String.format("http://127.0.0.1:%s%s", Constants.APPLICATION_PORT, Constants.VALIDATION_ROUTE_PATH);
    HttpPost request = new HttpPost(uri);
    request.setHeader("Content-type", "application/json");
    JsonObject json = new JsonObject().put("resourceId", "1");
    request.setEntity(new StringEntity(json.toString()));
    // act
    HttpResponse response = httpClient.execute(request);
    // assert
    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
  }

  @Test
  void routeNotFoundTest() throws IOException {
    // arrange
    String uri = String.format("http://127.0.0.1:%s%s", Constants.APPLICATION_PORT, "/nonexistent");
    // act
    HttpResponse response = httpClient.execute(new HttpGet(uri));
    // assert
    assertThat(response.getStatusLine().getStatusCode(), equalTo(404));
  }

}
