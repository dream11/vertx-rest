package com.dream11.rest.exception.mapper;

import com.dream11.rest.exception.ExceptionUtil;
import com.dream11.rest.exception.RestException;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

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
