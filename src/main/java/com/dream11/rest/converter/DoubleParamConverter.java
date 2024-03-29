package com.dream11.rest.converter;

import jakarta.ws.rs.ext.ParamConverter;
import java.lang.annotation.Annotation;

public class DoubleParamConverter extends BaseParamConverter implements ParamConverter<Double> {

  public DoubleParamConverter(Annotation[] annotations) {
    super(annotations);
  }

  @Override
  public Double fromString(String s) {
    return this.parseParam(s, Double::parseDouble);
  }

  @Override
  public String toString(Double d) {
    return d.toString();
  }
}
