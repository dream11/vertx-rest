package com.dream11.rest.util;


import static org.assertj.core.api.Assertions.assertThat;

import com.dream11.rest.exception.RestAppError;
import com.dream11.rest.exception.RestError;
import com.dream11.rest.exception.RestException;
import com.dream11.rest.exception.impl.RestErrorEnum;
import org.junit.jupiter.api.Test;

class ExceptionUtilTest {
  @Test
  void testParseThrowable() {
    // arrange
    RestException exception = new RestException("REST_EXCEPTION", "Rest exception", 500);
    // act
    Throwable throwable = ExceptionUtil.parseThrowable(exception);
    // assert
    assertThat(throwable.getClass()).isEqualTo(RestException.class);
    assertThat(throwable.getMessage()).isEqualTo(exception.getMessage());
    assertThat(((RestException) throwable).getHttpStatusCode()).isEqualTo(exception.getHttpStatusCode());
    assertThat(((RestException) throwable).getErrorCode()).isEqualTo(exception.getErrorCode());
  }

  @Test
  void testGenericParseThrowable() {
    // arrange
    String message = "Test exception";
    // act
    Throwable throwable = ExceptionUtil.parseThrowable(new RuntimeException(message));
    // assert
    assertThat(throwable.getClass()).isEqualTo(RestException.class);
    assertThat(((RestException) throwable).getHttpStatusCode()).isEqualTo(RestErrorEnum.UNKNOWN_EXCEPTION.getHttpStatusCode());
    assertThat(((RestException) throwable).getErrorCode()).isEqualTo(RestErrorEnum.UNKNOWN_EXCEPTION.getErrorCode());
    assertThat(throwable.getMessage()).isEqualTo(RestErrorEnum.UNKNOWN_EXCEPTION.getErrorMessage());
    assertThat(throwable.getCause().getMessage()).isEqualTo(message);
  }

  @Test
  void testGetException() {
    // arrange
    String errorMessage = "Error:Something went wrong, message:Error message";
    RestError restError = RestAppError.UNKNOWN_EXCEPTION;

    // act
    RestException restException = ExceptionUtil.getException(restError, "Something went wrong", "Error message");

    // assert
    assertThat(restException.getHttpStatusCode()).isEqualTo(restError.getHttpStatusCode());
    assertThat(restException.getErrorCode()).isEqualTo(restError.getErrorCode());
    assertThat(restException.getMessage()).isEqualTo(errorMessage);
    assertThat(restException.getMessage()).isEqualTo(restException.getErrorMessage());
  }

}
