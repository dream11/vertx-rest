package com.dream11.rest;


import static org.assertj.core.api.Assertions.assertThat;

import io.reactivex.rxjava3.core.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.rxjava3.core.MultiMap;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.core.buffer.Buffer;
import io.vertx.rxjava3.ext.web.client.HttpResponse;
import io.vertx.rxjava3.ext.web.client.WebClient;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@ExtendWith({VertxExtension.class, Setup.class})
@Slf4j
class RestApiIT {

  final WebClient webClient = WebClient.create(Vertx.vertx());

  @Test
  void nullHeaderTest() {
    // arrange
    String path = String.format("%s/1?testFilter=query&double=1.1&float=1.1&integer=1&long=1", Constants.VALIDATION_ROUTE_PATH);
    // act
    int statusCode = this.makeGetRequest(path)
        .map(HttpResponse::statusCode)
        .blockingGet();
    // assert
    assertThat(statusCode).isEqualTo(400);
  }

  @Test
  void nullQueryParamTest() {
    // arrange
    String path = String.format("%s/1", Constants.VALIDATION_ROUTE_PATH);
    MultiMap headers = MultiMap.caseInsensitiveMultiMap().add("testHeader", "1");
    // act
    int statusCode = this.makeGetRequest(path, headers)
        .map(HttpResponse::statusCode)
        .blockingGet();
    // assert
    assertThat(statusCode).isEqualTo(400);
  }

  @ParameterizedTest
  @CsvSource({"/class", "/method"})
  void timeoutAnnotationTest(String param) {
    // arrange
    String path = String.format("%s%s", Constants.TIMEOUT_ROUTE_PATH, param);
    // act
    int statusCode = this.makeGetRequest(path)
        .map(HttpResponse::statusCode)
        .blockingGet();
    // assert
    assertThat(statusCode).isEqualTo(503);
  }

  @ParameterizedTest
  @CsvSource({"integer", "long", "float", "double"})
  void typeTypeValidationParamTest(String param) {
    // arrange
    String path = String.format("%s/1?testFilter=query&%s=param", Constants.VALIDATION_ROUTE_PATH, param);
    MultiMap headers = MultiMap.caseInsensitiveMultiMap().add("testHeader", "1");
    // act
    HttpResponse<Buffer> response = this.makeGetRequest(path, headers)
        .blockingGet();
    JsonObject responseBody = response.bodyAsJsonObject();
    // assert
    assertThat(response.statusCode()).isEqualTo(400);
    assertThat(responseBody.getJsonObject("error").getString("code")).isEqualTo("BAD_REQUEST");
    assertThat(responseBody.getJsonObject("error").getString("message")).isEqualTo(
        String.format("Query param '%s' must be %s", param, param));
  }

  @Test
  void validateBodyTest() {
    // arrange
    String path = String.format("%s", Constants.VALIDATION_ROUTE_PATH);
    JsonObject body = new JsonObject().put("resourceId", "Hello");
    // act
    HttpResponse<Buffer> response = this.makePostRequest(path, body)
        .blockingGet();
    JsonObject responseBody = response.bodyAsJsonObject();
    // assert
    assertThat(response.statusCode()).isEqualTo(400);
    assertThat(responseBody.getJsonObject("error").getString("code")).isEqualTo("BAD_REQUEST");
    assertThat(responseBody.getJsonObject("error").getString("message")).isEqualTo("resourceId must be integer");
  }

  @Test
  void positiveBodyTest() {
    // arrange
    String path = String.format("%s", Constants.VALIDATION_ROUTE_PATH);
    JsonObject body = new JsonObject().put("resourceId", "1");
    // act
    HttpResponse<Buffer> response = this.makePostRequest(path, body)
        .blockingGet();
    // assert
    assertThat(response.statusCode()).isEqualTo(200);
  }

  @Test
  void routeNotFoundTest() {
    // arrange
    String path = "/nonexistent";
    // act
    int statusCode = this.makeGetRequest(path)
        .map(HttpResponse::statusCode)
        .blockingGet();
    // assert
    assertThat(statusCode).isEqualTo(404);
  }


  @Test
  @SneakyThrows
  void testBackPressure() {
    // arrange
    JsonObject body = JsonObject.of("resourceId", "1");
    List<Single<HttpResponse<Buffer>>> responseSingles = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      responseSingles.add(this.makePostRequest(Constants.VALIDATION_ROUTE_PATH, body));
    }

    // act
    List<Integer> statusCodes = Single.merge(responseSingles)
        .map(HttpResponse::statusCode)
        .toList()
        .blockingGet();

    // assert
    assertThat(statusCodes).contains(200, 503);
  }

  private Single<HttpResponse<Buffer>> makePostRequest(String path, JsonObject body) {
    String uri = String.format("http://127.0.0.1:%s%s", Constants.APPLICATION_PORT, path);
    return this.webClient.postAbs(uri)
        .putHeader("Content-type", "application/json")
        .rxSendJsonObject(body);
  }

  private Single<HttpResponse<Buffer>> makeGetRequest(String path, MultiMap headers) {
    String uri = String.format("http://127.0.0.1:%s%s", Constants.APPLICATION_PORT, path);
    return this.webClient.getAbs(uri)
        .putHeaders(headers)
        .rxSend();
  }

  private Single<HttpResponse<Buffer>> makeGetRequest(String path) {
    return makeGetRequest(path, MultiMap.caseInsensitiveMultiMap());
  }
}
