package com.dream11.rest.exception;

import io.vertx.core.json.JsonObject;

public class RestException extends RuntimeException {

  private final RestError error;

  public RestException(RestError restError) {
    super(restError.getErrorMessage());
    this.error = restError;
  }

  public RestException(RestError restError, Throwable cause) {
    super(restError.getErrorMessage(), cause);
    this.error = restError;
  }

  public RestError getError() {
    return error;
  }

  public int getHttpStatusCode() {
    return error.getHttpStatusCode();
  }

  public JsonObject toJson() {
    JsonObject errorJson = new JsonObject()
        .put("message", this.error.getErrorMessage())
        .put("cause", this.getCause() == null ? this.getMessage() : this.getCause().getMessage())
        .put("code", this.error.getErrorCode());
    return new JsonObject()
        .put("error", errorJson);
  }

  @Override
  public String toString() {
    return this.toJson().toString();
  }
}
