package com.dream11.rest.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public enum RestAppError implements RestError {
  UNKNOWN_EXCEPTION("UNKNOWN_EXCEPTION", "Error:%s, message:%s", 500);

  final String errorCode;
  final String errorMessage;
  final int httpStatusCode;
}
