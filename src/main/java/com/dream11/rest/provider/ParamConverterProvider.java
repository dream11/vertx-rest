package com.dream11.rest.provider;

import com.dream11.rest.converter.DoubleParamConverter;
import com.dream11.rest.converter.FloatParamConverter;
import com.dream11.rest.converter.IntegerParamConverter;
import com.dream11.rest.converter.LongParamConverter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.Provider;

@Provider
public class ParamConverterProvider implements javax.ws.rs.ext.ParamConverterProvider {
  @Override
  public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
    if (rawType == Long.class) {
      return (ParamConverter<T>) new LongParamConverter(annotations);
    } else if (rawType == Integer.class) {
      return (ParamConverter<T>) new IntegerParamConverter(annotations);
    } else if (rawType == Double.class) {
      return (ParamConverter<T>) new DoubleParamConverter(annotations);
    } else if (rawType == Float.class) {
      return (ParamConverter<T>) new FloatParamConverter(annotations);
    }
    return null;
  }
}
