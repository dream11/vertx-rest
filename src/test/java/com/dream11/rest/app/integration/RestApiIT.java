package com.dream11.rest.app.integration;

import com.dream11.rest.app.Constants;
import com.dream11.rest.app.Setup;
import com.dream11.rest.app.inject.AppContext;
import com.dream11.rest.app.module.MainModule;
import com.dream11.rest.app.verticle.RestVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.reactivex.core.Vertx;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@ExtendWith({VertxExtension.class, Setup.class})
@Slf4j
public class RestApiIT {

  private final CloseableHttpClient httpClient = HttpClients.createDefault();
  private static final Vertx vertx = Vertx.vertx();

  @BeforeAll
  public static void setup() {
    AppContext.initialize(Arrays.asList(new MainModule[] {new MainModule(vertx)}));
    final String verticleName = RestVerticle.class.getName();
    String verticleId = vertx.rxDeployVerticle(AppContext.getContextInstance().getInstance(RestVerticle.class))
        .doOnError(error -> log.error("Error in deploying verticle : {}", verticleName, error))
        .doOnSuccess(v -> log.info("Deployed verticle : {}", verticleName))
        .blockingGet();
  }

  @Test
  public void healthCheckTest() throws IOException {
    String uri = String.format("http://127.0.0.1:%s%s", Constants.APPLICATION_PORT, Constants.HEALTHCHECK_ROUTE_PATH);
    HttpResponse response = httpClient.execute(new HttpGet(uri));
    MatcherAssert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(200));
    MatcherAssert.assertThat(IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8),
        Matchers.equalTo(Constants.HEALTHCHECK_RESPONSE));
  }

  @Test
  public void nullHeaderTest() throws IOException {
    String uri = String.format("http://127.0.0.1:%s%s/1?testFilter=query", Constants.APPLICATION_PORT, Constants.VALIDATION_ROUTE_PATH);
    HttpResponse response = httpClient.execute(new HttpGet(uri));
    MatcherAssert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(400));
  }

  @Test
  public void nullQueryParamTest() throws IOException {
    String uri = String.format("http://127.0.0.1:%s%s/1", Constants.APPLICATION_PORT, Constants.VALIDATION_ROUTE_PATH);
    HttpGet request = new HttpGet(uri);
    request.setHeader("testHeader", "1");
    HttpResponse response = httpClient.execute(request);
    MatcherAssert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(400));
  }

  @Test
  public void timeOutTest() throws IOException {
    String uri = String.format("http://127.0.0.1:%s%s", Constants.APPLICATION_PORT, Constants.TIMEOUT_ROUTE_PATH);
    HttpResponse response = httpClient.execute(new HttpGet(uri));
    MatcherAssert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(503));
  }

  @Test
  public void integerTypeValidationParamTest() throws IOException {
    String uri = String.format("http://127.0.0.1:%s%s/1?testFilter=query&integerParam=integer", Constants.APPLICATION_PORT,
        Constants.VALIDATION_ROUTE_PATH);
    HttpGet request = new HttpGet(uri);
    request.setHeader("testHeader", "1");
    HttpResponse response = httpClient.execute(request);
    JsonObject responseBody = new JsonObject(IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8));
    MatcherAssert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(400));
    MatcherAssert.assertThat(responseBody.getJsonObject("error").getString("code"), Matchers.equalTo("BAD_REQUEST"));
    MatcherAssert.assertThat(responseBody.getJsonObject("error").getString("message"),
        Matchers.equalTo("Query param 'intParam' must be integer"));

  }

  @Test
  public void longTypeValidationParamTest() throws IOException {
    String uri = String.format("http://127.0.0.1:%s%s/1?testFilter=query&longParam=long", Constants.APPLICATION_PORT,
        Constants.VALIDATION_ROUTE_PATH);
    HttpGet request = new HttpGet(uri);
    request.setHeader("testHeader", "1");
    HttpResponse response = httpClient.execute(request);
    JsonObject responseBody = new JsonObject(IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8));
    MatcherAssert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(400));
    MatcherAssert.assertThat(responseBody.getJsonObject("error").getString("code"), Matchers.equalTo("BAD_REQUEST"));
    MatcherAssert.assertThat(responseBody.getJsonObject("error").getString("message"),
        Matchers.equalTo("Query param 'longParam' must be long"));

  }

  @Test
  public void floatTypeValidationParamTest() throws IOException {
    String uri = String.format("http://127.0.0.1:%s%s/1?testFilter=query&floatParam=float", Constants.APPLICATION_PORT,
        Constants.VALIDATION_ROUTE_PATH);
    HttpGet request = new HttpGet(uri);
    request.setHeader("testHeader", "1");
    HttpResponse response = httpClient.execute(request);
    JsonObject responseBody = new JsonObject(IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8));
    MatcherAssert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(400));
    MatcherAssert.assertThat(responseBody.getJsonObject("error").getString("code"), Matchers.equalTo("BAD_REQUEST"));
    MatcherAssert.assertThat(responseBody.getJsonObject("error").getString("message"),
        Matchers.equalTo("Query param 'floatParam' must be float"));

  }

  @Test
  public void doubleTypeValidationParamTest() throws IOException {
    String uri = String.format("http://127.0.0.1:%s%s/1?testFilter=query&doubleParam=double", Constants.APPLICATION_PORT,
        Constants.VALIDATION_ROUTE_PATH);
    HttpGet request = new HttpGet(uri);
    request.setHeader("testHeader", "1");
    HttpResponse response = httpClient.execute(request);
    JsonObject responseBody = new JsonObject(IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8));
    MatcherAssert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(400));
    MatcherAssert.assertThat(responseBody.getJsonObject("error").getString("code"), Matchers.equalTo("BAD_REQUEST"));
    MatcherAssert.assertThat(responseBody.getJsonObject("error").getString("message"),
        Matchers.equalTo("Query param 'doubleParam' must be double"));

  }

  @Test
  public void validateBodyTest() throws IOException {
    String uri = String.format("http://127.0.0.1:%s%s", Constants.APPLICATION_PORT, Constants.VALIDATION_ROUTE_PATH);
    HttpPost request = new HttpPost(uri);
    request.setHeader("Content-type", "application/json");
    JsonObject json = new JsonObject().put("resourceId", "Hello");
    request.setEntity(new StringEntity(json.toString()));
    HttpResponse response = httpClient.execute(request);
    JsonObject responseBody = new JsonObject(IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8));
    MatcherAssert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(400));
    MatcherAssert.assertThat(responseBody.getJsonObject("error").getString("code"), Matchers.equalTo("BAD_REQUEST"));
    MatcherAssert.assertThat(responseBody.getJsonObject("error").getString("message"),
        Matchers.equalTo("resourceId must be integer"));
  }
}
