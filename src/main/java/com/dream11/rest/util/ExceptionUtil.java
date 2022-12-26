package com.dream11.rest.util;

import com.dream11.rest.exception.RestError;
import com.dream11.rest.exception.RestException;
import com.dream11.rest.exception.impl.RestErrorEnum;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionUtil {

  public Throwable parseThrowable(Throwable throwable) {
    return throwable instanceof RestException ? throwable :
        new RestException(RestErrorEnum.UNKNOWN_EXCEPTION, throwable);
  }

  public RestException getException(RestError restError, Object... params) {
    String message = String.format(restError.getErrorMessage(), params);
    return new RestException(restError.getErrorCode(), message, restError.getHttpStatusCode());
  }
}
