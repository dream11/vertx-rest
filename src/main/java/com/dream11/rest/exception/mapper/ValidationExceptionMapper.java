package com.dream11.rest.exception.mapper;

import com.dream11.rest.exception.RestException;
import com.dream11.rest.exception.impl.RestErrorEnum;
import com.dream11.rest.util.ExceptionUtil;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

  public Response toResponse(ValidationException validationException) {
    log.error("Constraint violation", validationException);
    RestException restException = ExceptionUtil.getException(RestErrorEnum.INVALID_REQUEST, validationException.getMessage());
    return Response.status(Response.Status.BAD_REQUEST).entity(restException.toString()).build();
  }
}
