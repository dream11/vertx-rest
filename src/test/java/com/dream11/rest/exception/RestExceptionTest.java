package com.dream11.rest.exception;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class RestExceptionTest {
  @Test
  void testRestException() {
    // arrange
    String errorMessage = "An error has occurred";
    Throwable cause = new RuntimeException(errorMessage);
    RestError restError = RestAppError.UNKNOWN_EXCEPTION;

    // act
    RestException restException = new RestException(restError);
    RestException restExceptionWithCause = new RestException(restError, cause);
    // assert
    assertThat(restException.getMessage(), equalTo(restError.getErrorMessage()));
    assertThat(restException.getHttpStatusCode(), equalTo(restError.getHttpStatusCode()));
    assertThat(restException.getErrorCode(), equalTo(restError.getErrorCode()));

    assertThat(restExceptionWithCause.getMessage(), equalTo(restError.getErrorMessage()));
    assertThat(restExceptionWithCause.getHttpStatusCode(), equalTo(restError.getHttpStatusCode()));
    assertThat(restExceptionWithCause.getErrorCode(), equalTo(restError.getErrorCode()));
    assertThat(restExceptionWithCause.getCause(), equalTo(cause));
  }
}
