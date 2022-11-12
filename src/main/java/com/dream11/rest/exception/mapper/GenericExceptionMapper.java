package com.dream11.rest.exception.mapper;

import com.dream11.rest.exception.ExceptionUtil;
import com.dream11.rest.exception.RestException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
  @Override
  public Response toResponse(Throwable throwable) {
    RestException restException = (RestException) ExceptionUtil.parseThrowable(throwable);
    log.error("Error: " + restException.getError().getErrorCode() + " " + restException.getHttpStatusCode()
        + " " + restException.getError().getErrorMessage(), throwable);
    return Response.status(restException.getHttpStatusCode()).entity(restException.toString()).build();
  }
}
