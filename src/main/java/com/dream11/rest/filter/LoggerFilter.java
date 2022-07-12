package com.dream11.rest.filter;

import com.dream11.rest.RestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Provider
public class LoggerFilter extends RequestResponseFilter {

  private static final String REQUEST_START_TIME = "REQUEST_START_TIME";

  @Override
  protected String getResponseString(Object response) throws JsonProcessingException {
    return RestUtil.getString(response);
  }

  @Override
  public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext)
          throws IOException {
    super.filter(containerRequestContext, containerResponseContext);
    if (containerRequestContext.getProperty(REQUEST_START_TIME) != null) {
      log.debug("[RESPONSE TIME] Time taken for route: {} {} : {}ms, Status code : {}", containerRequestContext.getMethod(),
          containerRequestContext.getUriInfo().getPath(),
          System.currentTimeMillis() - (Long) containerRequestContext.getProperty(REQUEST_START_TIME),
          containerResponseContext.getStatus()
      );
      containerRequestContext.removeProperty(REQUEST_START_TIME);
    }
  }

  @Override
  public void filter(ContainerRequestContext containerRequestContext) throws IOException {
    containerRequestContext.setProperty(REQUEST_START_TIME, System.currentTimeMillis());

    log.debug("STATED REQUEST : {} {}", containerRequestContext.getMethod(), containerRequestContext.getUriInfo().getPath());
    String body = "{}";
    if (containerRequestContext.hasEntity() && containerRequestContext.getEntityStream() != null) {
      body = IOUtils.toString(containerRequestContext.getEntityStream(), StandardCharsets.UTF_8).replace("\n", " ");
      containerRequestContext.getEntityStream().reset();
    }
    log.debug("Path: {} {}  Request: Body : {}, Headers : {}, PathParams : {}, QueryParams : {}", containerRequestContext.getMethod(),
        containerRequestContext.getUriInfo().getPath(), body, containerRequestContext.getHeaders(),
        containerRequestContext.getUriInfo().getPathParameters(),
        containerRequestContext.getUriInfo().getQueryParameters());
  }
}
