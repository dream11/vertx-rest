package com.dream11.rest.filter;

import com.dream11.rest.annotation.Timeout;
import com.dream11.rest.exception.RestException;
import io.vertx.rxjava3.core.Vertx;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.jboss.resteasy.core.interception.jaxrs.PostMatchContainerRequestContext;


@Slf4j
@Provider
public class TimeoutFilter implements ContainerRequestFilter, ContainerResponseFilter {

  static final String TIMER_ID = "__TIMER_ID__";
  static final Long DEFAULT_TIMEOUT = 20000L;
  static final int DEFAULT_HTTP_STATUS_CODE = HttpStatus.SC_INTERNAL_SERVER_ERROR;
  final Vertx vertx = Vertx.currentContext().owner();
  @Context
  private ResourceInfo resourceInfo;

  /**
   * Cancel the vertx timer created for timeout.
   *
   * @param containerRequestContext  requestContext
   * @param containerResponseContext responseContext
   */
  @Override
  public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) {
    if (containerRequestContext.getProperty(TIMER_ID) != null) {
      vertx.cancelTimer((Long) containerRequestContext.getProperty(TIMER_ID));
      containerRequestContext.removeProperty(TIMER_ID);
    }
  }

  /**
   * Gets timeout from annotation and create a vertx timer that throws RestException after timeout period.
   *
   * @param containerRequestContext requestContext
   */
  @Override
  public void filter(ContainerRequestContext containerRequestContext) {
    Timeout resourceMethodAnnotation = resourceInfo.getResourceMethod().getAnnotation(Timeout.class);
    Timeout resourceClassAnnotation = resourceInfo.getResourceClass().getAnnotation(Timeout.class);
    // check timeout annotation on resource method then check on resource class
    long timeout;
    int httpStatusCode;
    if (resourceMethodAnnotation == null) {
      timeout = resourceClassAnnotation == null ? DEFAULT_TIMEOUT : resourceClassAnnotation.value();
      httpStatusCode = resourceClassAnnotation == null ? DEFAULT_HTTP_STATUS_CODE : resourceClassAnnotation.httpStatusCode();
    } else {
      timeout = resourceMethodAnnotation.value();
      httpStatusCode = resourceMethodAnnotation.httpStatusCode();
    }
    AsyncResponse asyncResponse =
        ((PostMatchContainerRequestContext) containerRequestContext).getHttpRequest().getAsyncContext().getAsyncResponse();

    // not using asyncResponse.setTimeout(timeout) because it creates a vertx timer but do not cancel it upon request completion
    if (timeout > 0 && !asyncResponse.isCancelled() && !asyncResponse.isDone()) {
      long timerId = vertx.setTimer(timeout, id -> asyncResponse.resume(new RestException(
          "REQUEST_TIMEOUT", String.format("Request timed out after %dms", timeout), httpStatusCode)));
      containerRequestContext.setProperty(TIMER_ID, timerId);
    }
  }
}
