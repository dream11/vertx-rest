package com.dream11.rest.exception;

import com.dream11.rest.exception.impl.RestErrorEnum;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
public class ExceptionUtil {

  public static Throwable parseThrowable(Throwable throwable) {
    return throwable instanceof RestException ? throwable :
        new RestException(RestError.of(RestErrorEnum.UNKNOWN_EXCEPTION));
  }
}
