package com.dream11.rest.exception.mapper;

import com.dream11.rest.exception.RestError;
import com.dream11.rest.exception.RestException;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

@Slf4j
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

  public Response toResponse(WebApplicationException webApplicationException) {
    log.error("Error occurred", webApplicationException);
    RestException restException =
        webApplicationException.getCause() instanceof RestException ?
            (RestException) webApplicationException.getCause() :
            new RestException(RestError.of("UNSUPPORTED_REQUEST",
                    webApplicationException.getResponse().getStatusInfo().getReasonPhrase(),
                    webApplicationException.getResponse().getStatus()), webApplicationException);
    return Response.status(restException.getHttpStatusCode()).entity(restException.toString()).build();
  }
}
