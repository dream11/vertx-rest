package com.dream11.rest.exception.impl;

import com.dream11.rest.exception.RestError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RestErrorEnum implements RestError {

  UNKNOWN_EXCEPTION("UNKNOWN_EXCEPTION", "An error has occurred", HttpStatus.SC_INTERNAL_SERVER_ERROR),
  UNSUPPORTED_REQUEST("UNSUPPORTED_REQUEST", "The request could not be processed", HttpStatus.SC_BAD_REQUEST),
  INVALID_REQUEST("INVALID_REQUEST", "The request violates one or more constraints", HttpStatus.SC_BAD_REQUEST);

  final String errorCode;
  final String errorMessage;
  final int httpStatusCode;
}
