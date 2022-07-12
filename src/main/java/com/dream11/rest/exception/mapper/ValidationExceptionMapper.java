package com.dream11.rest.exception.mapper;

import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.dream11.rest.exception.RestError;
import com.dream11.rest.exception.RestException;
import com.dream11.rest.exception.impl.RestErrorEnum;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

  public Response toResponse(ValidationException validationException) {
    log.error("Constraint violation", validationException);
    RestException restException =
        new RestException(RestError.of(RestErrorEnum.INVALID_REQUEST));
    return Response.status(Response.Status.BAD_REQUEST).entity(restException.toString()).build();
  }
}
