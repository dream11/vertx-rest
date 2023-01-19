package com.dream11.rest.util;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.reflections.Reflections;

@UtilityClass
public class AnnotationUtil {
  public List<Class<?>> getClassesWithAnnotation(String packageName, Class<? extends Annotation> annotation) {
    Reflections ref = new Reflections(packageName);
    return new ArrayList<>(ref.getTypesAnnotatedWith(annotation));
  }
}
