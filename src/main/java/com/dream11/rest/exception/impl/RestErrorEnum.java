package com.dream11.rest.exception.impl;

import com.dream11.rest.exception.RestError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RestErrorEnum implements RestError {

  UNKNOWN_EXCEPTION("UNKNOWN_EXCEPTION", "An error has occurred", HttpStatus.SC_INTERNAL_SERVER_ERROR),
  INVALID_REQUEST("INVALID_REQUEST", "The request violates one or more constraints", HttpStatus.SC_BAD_REQUEST);

  final String errorCode;
  final String errorMessage;
  final int httpStatusCode;
}
