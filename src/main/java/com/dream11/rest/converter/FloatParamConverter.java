package com.dream11.rest.converter;

import javax.ws.rs.ext.ParamConverter;
import java.lang.annotation.Annotation;

public class FloatParamConverter extends BaseParamConverter implements ParamConverter<Float> {

  public FloatParamConverter(Annotation[] annotations) {
    super(annotations);
  }

  @Override
  public Float fromString(String s) {
    return this.parseParam(s, Float::parseFloat);
  }

  @Override
  public String toString(Float f) {
    return f.toString();
  }
}
