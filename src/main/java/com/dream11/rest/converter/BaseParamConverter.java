package com.dream11.rest.converter;

import com.dream11.rest.annotation.TypeValidationError;
import com.dream11.rest.exception.RestError;
import com.dream11.rest.exception.RestException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import lombok.Getter;

public class BaseParamConverter {

  @Getter
  private RestError error;

  public BaseParamConverter(Annotation[] annotations) {
    Optional<TypeValidationError> optionalAnnotation =
        Arrays.stream(annotations)
            .filter(annotation -> annotation.annotationType() == TypeValidationError.class)
            .map(TypeValidationError.class::cast)
            .findAny();
    optionalAnnotation.ifPresent(typeValidationError -> {
      this.error = RestError.of(typeValidationError.code(), typeValidationError.message(), typeValidationError.httpStatusCode());
    });
  }

  protected <T> T parseParam(String s, Function<String, T> parseMethod) {
    try {
      return parseMethod.apply(s);
    } catch (Exception e) {
      if (this.getError() != null) {
        throw new RestException(this.getError(), e);
      } else {
        throw e;
      }
    }
  }
}
