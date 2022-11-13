package com.dream11.rest.unit;

import com.dream11.rest.exception.ExceptionUtil;
import com.dream11.rest.exception.RestError;
import com.dream11.rest.exception.RestException;
import com.dream11.rest.exception.impl.RestErrorEnum;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

class ExceptionUtilTest {
  @Test
  void testParseThrowable() {
    RestException exception = new RestException(RestError.of("REST_EXCEPTION", "Rest exception", 500));
    Throwable throwable = ExceptionUtil.parseThrowable(exception);
    assert throwable instanceof RestException;
    MatcherAssert.assertThat(throwable.getMessage(), Matchers.equalTo(exception.getMessage()));
    MatcherAssert.assertThat(((RestException) throwable).getHttpStatusCode(), Matchers.equalTo(exception.getHttpStatusCode()));
    MatcherAssert.assertThat(((RestException) throwable).getError().getErrorCode(), Matchers.equalTo(exception.getError().getErrorCode()));
  }

  @Test
  void testGenericParseThrowable() {
    String message = "Test exception";
    Throwable throwable = ExceptionUtil.parseThrowable(new RuntimeException(message));
    assert throwable instanceof RestException;
    MatcherAssert.assertThat(((RestException) throwable).getHttpStatusCode(),
        Matchers.equalTo(RestErrorEnum.UNKNOWN_EXCEPTION.getHttpStatusCode()));
    MatcherAssert.assertThat(((RestException) throwable).getError().getErrorCode(),
        Matchers.equalTo(RestErrorEnum.UNKNOWN_EXCEPTION.getErrorCode()));
    MatcherAssert.assertThat(throwable.getMessage(), Matchers.equalTo(RestErrorEnum.UNKNOWN_EXCEPTION.getErrorMessage()));
    MatcherAssert.assertThat(throwable.getCause().getMessage(), Matchers.equalTo(message));
  }
}
