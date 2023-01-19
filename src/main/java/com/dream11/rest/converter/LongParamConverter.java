package com.dream11.rest.converter;


import jakarta.ws.rs.ext.ParamConverter;
import java.lang.annotation.Annotation;

public class LongParamConverter extends BaseParamConverter implements ParamConverter<Long> {

  public LongParamConverter(Annotation[] annotations) {
    super(annotations);
  }

  @Override
  public Long fromString(String s) {
    return this.parseParam(s, Long::parseLong);
  }

  @Override
  public String toString(Long l) {
    return l.toString();
  }
}
