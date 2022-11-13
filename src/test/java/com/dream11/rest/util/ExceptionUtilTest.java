package com.dream11.rest.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

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
    assertThat(throwable.getClass(), equalTo(RestException.class));
    assertThat(throwable.getMessage(), equalTo(exception.getMessage()));
    assertThat(((RestException) throwable).getHttpStatusCode(), equalTo(exception.getHttpStatusCode()));
    assertThat(((RestException) throwable).getErrorCode(), equalTo(exception.getErrorCode()));
  }

  @Test
  void testGenericParseThrowable() {
    // arrange
    String message = "Test exception";
    // act
    Throwable throwable = ExceptionUtil.parseThrowable(new RuntimeException(message));
    // assert
    assertThat(throwable.getClass(), equalTo(RestException.class));
    assertThat(((RestException) throwable).getHttpStatusCode(), equalTo(RestErrorEnum.UNKNOWN_EXCEPTION.getHttpStatusCode()));
    assertThat(((RestException) throwable).getErrorCode(), equalTo(RestErrorEnum.UNKNOWN_EXCEPTION.getErrorCode()));
    assertThat(throwable.getMessage(), equalTo(RestErrorEnum.UNKNOWN_EXCEPTION.getErrorMessage()));
    assertThat(throwable.getCause().getMessage(), equalTo(message));
  }

  @Test
  void testGetException() {
    // arrange
    String errorMessage = "Error:Something went wrong, message:Error message";
    RestError restError = RestAppError.UNKNOWN_EXCEPTION;

    // act
    RestException restException = ExceptionUtil.getException(restError, "Something went wrong", "Error message");

    // assert
    assertThat(restException.getHttpStatusCode(), equalTo(restError.getHttpStatusCode()));
    assertThat(restException.getErrorCode(), equalTo(restError.getErrorCode()));
    assertThat(restException.getMessage(), equalTo(errorMessage));
    assertThat(restException.getMessage(), equalTo(restException.getErrorMessage()));
  }

}
