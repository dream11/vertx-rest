package com.dream11.rest.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;

public abstract class RequestResponseFilter<T> implements ContainerResponseFilter, ContainerRequestFilter {
    protected abstract String getResponseString(Object response) throws IOException;

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {
        if (containerResponseContext.hasEntity()) {
            containerResponseContext.setEntity(getResponseString(containerResponseContext.getEntity()));
        }
    }
}
