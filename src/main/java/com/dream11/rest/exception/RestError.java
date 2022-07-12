package com.dream11.rest.exception;

import com.dream11.rest.exception.impl.RestErrorImpl;

public interface RestError {

  String getErrorCode();

  String getErrorMessage();

  int getHttpStatusCode();

  static RestError of(String errorCode, String errorMessage, int httpStatusCode) {
    return new RestErrorImpl(errorCode, errorMessage, httpStatusCode);
  }

  static RestError of(RestError error) {
    return new RestErrorImpl(error);
  }

}