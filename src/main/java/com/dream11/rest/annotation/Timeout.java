package com.dream11.rest.annotation;

import org.apache.http.HttpStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Can be applied on JAX-RS method or class.
 * Annotation on method overrides annotation on class.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Timeout {
  long value(); // ms
  int httpStatusCode() default HttpStatus.SC_INTERNAL_SERVER_ERROR;
}
