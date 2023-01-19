package com.dream11.rest.converter;

import com.dream11.rest.annotation.TypeValidationError;
import com.dream11.rest.exception.RestException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

public class BaseParamConverter {

  String errorMessage;
  String errorCode;
  Integer httpStatusCode;

  public BaseParamConverter(Annotation[] annotations) {
    Optional<TypeValidationError> optionalAnnotation =
        Arrays.stream(annotations)
            .filter(annotation -> annotation.annotationType() == TypeValidationError.class)
            .map(TypeValidationError.class::cast)
            .findAny();
    optionalAnnotation.ifPresent(typeValidationError -> {
      this.errorCode = typeValidationError.code();
      this.errorMessage = typeValidationError.message();
      this.httpStatusCode = typeValidationError.httpStatusCode();
    });
  }

  protected <T> T parseParam(String s, Function<String, T> parseMethod) {
    try {
      return parseMethod.apply(s);
    } catch (Exception e) {
      if (this.errorMessage != null) {
        throw new RestException(this.errorCode, this.errorMessage, this.httpStatusCode, e);
      } else {
        throw e;
      }
    }
  }
}
