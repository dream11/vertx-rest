package com.dream11.rest.exception;

public interface RestError {

  String getErrorCode();

  String getErrorMessage();

  int getHttpStatusCode();

  default Error getError() {
    return Error.of(this.getErrorCode(), this.getErrorMessage());
  }
}