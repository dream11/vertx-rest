package com.dream11.rest.exception.mapper;

import com.dream11.rest.exception.RestException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

  public Response toResponse(WebApplicationException webApplicationException) {
    log.error("Error occurred", webApplicationException);
    RestException restException =
        webApplicationException.getCause() instanceof RestException ? (RestException) webApplicationException.getCause() :
            new RestException("UNSUPPORTED_REQUEST",
                webApplicationException.getResponse().getStatusInfo().getReasonPhrase(),
                webApplicationException.getResponse().getStatus(), webApplicationException);
    return Response.status(restException.getHttpStatusCode()).entity(restException.toString()).build();
  }
}
