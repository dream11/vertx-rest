package com.dream11.rest.filter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;

import java.io.IOException;

public abstract class RequestResponseFilter implements ContainerResponseFilter, ContainerRequestFilter {
    protected abstract String getResponseString(Object response) throws IOException;

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {
        if (containerResponseContext.hasEntity()) {
            containerResponseContext.setEntity(getResponseString(containerResponseContext.getEntity()));
        }
    }
}
