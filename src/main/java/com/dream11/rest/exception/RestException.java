package com.dream11.rest.exception;

import io.vertx.core.json.JsonObject;
import lombok.Getter;

@Getter
public class RestException extends RuntimeException {

  final String errorMessage;
  final String errorCode;
  final Integer httpStatusCode;

  public RestException(String errorCode, String errorMessage, Integer httpStatusCode) {
    this(errorCode, errorMessage, httpStatusCode, null);
  }

  public RestException(String errorCode, String errorMessage, Integer httpStatusCode, Throwable throwable) {
    super(errorMessage, throwable);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
    this.httpStatusCode = httpStatusCode;
  }

  public RestException(RestError restError) {
    this(restError, null);
  }

  public RestException(RestError restError, Throwable cause) {
    super(restError.getErrorMessage(), cause);
    this.errorCode = restError.getErrorCode();
    this.errorMessage = restError.getErrorMessage();
    this.httpStatusCode = restError.getHttpStatusCode();
  }

  public JsonObject toJson() {
    JsonObject errorJson = new JsonObject()
        .put("message", this.errorMessage)
        .put("cause", this.getCause() == null ? this.getMessage() : this.getCause().getMessage())
        .put("code", this.errorCode);
    return new JsonObject()
        .put("error", errorJson);
  }

  @Override
  public String toString() {
    return this.toJson().toString();
  }
}
