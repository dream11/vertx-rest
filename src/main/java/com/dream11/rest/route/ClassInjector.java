package com.dream11.rest.route;

public interface ClassInjector {
    /**
     * Returns the appropriate instance for the given injection type
     */
    <T> T getInstance(Class<T> clazz);
}
