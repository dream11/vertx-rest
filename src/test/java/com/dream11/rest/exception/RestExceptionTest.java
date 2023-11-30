package com.dream11.rest.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


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
    assertThat(restException.getMessage()).isEqualTo(restError.getErrorMessage());
    assertThat(restException.getHttpStatusCode()).isEqualTo(restError.getHttpStatusCode());
    assertThat(restException.getErrorCode()).isEqualTo(restError.getErrorCode());

    assertThat(restExceptionWithCause.getMessage()).isEqualTo(restError.getErrorMessage());
    assertThat(restExceptionWithCause.getHttpStatusCode()).isEqualTo(restError.getHttpStatusCode());
    assertThat(restExceptionWithCause.getErrorCode()).isEqualTo(restError.getErrorCode());
    assertThat(restExceptionWithCause.getCause()).isEqualTo(cause);
  }
}
