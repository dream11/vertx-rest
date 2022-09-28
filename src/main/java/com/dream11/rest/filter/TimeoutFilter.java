package com.dream11.rest.filter;

import com.dream11.rest.annotation.Timeout;
import com.dream11.rest.exception.RestError;
import com.dream11.rest.exception.RestException;
import io.vertx.reactivex.core.Vertx;
import jakarta.ws.rs.container.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.core.interception.jaxrs.PostMatchContainerRequestContext;


@Slf4j
@Provider
public class TimeoutFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String TIMER_ID = "__TIMER_ID__";
    private final Vertx vertx = Vertx.currentContext().owner();
    @Context
    private ResourceInfo resourceInfo;

    /**
     * Cancel the vertx timer timer created for timeout
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
        long timeout = resourceMethodAnnotation == null ?
                resourceClassAnnotation == null ? 20000L : resourceClassAnnotation.value() :
                resourceMethodAnnotation.value();
        AsyncResponse asyncResponse =
                ((PostMatchContainerRequestContext) containerRequestContext).getHttpRequest().getAsyncContext().getAsyncResponse();

        // not using asyncResponse.setTimeout(timeout) because it creates a vertx timer but do not cancel it upon request completion
        if (timeout > 0 && !asyncResponse.isCancelled() && !asyncResponse.isDone()) {
            long timerId = vertx.setTimer(timeout, id -> asyncResponse.resume(new RestException(
                    RestError.of("REQUEST_TIMEOUT", String.format("Request timed out after %dms", timeout), 503))));
            containerRequestContext.setProperty(TIMER_ID, timerId);
        }
    }
}
