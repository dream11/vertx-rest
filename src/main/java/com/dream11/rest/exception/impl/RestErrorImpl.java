package com.dream11.rest.exception.impl;

import com.dream11.rest.exception.RestError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public class RestErrorImpl implements RestError {
  private final String errorCode;
  private final String errorMessage;
  private final int httpStatusCode;

  public RestErrorImpl(RestError error) {
    this.errorCode = error.getErrorCode();
    this.errorMessage = error.getErrorMessage();
    this.httpStatusCode = error.getHttpStatusCode();
  }
}