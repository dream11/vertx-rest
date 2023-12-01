package com.dream11.rest.util;

import com.dream11.rest.Constants;
import com.dream11.rest.route.TimeoutRoute;
import com.dream11.rest.route.ValidationCheckRoute;
import jakarta.ws.rs.Path;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AnnotationUtilTest {
  @Test
  void testAnnotatedClasses() {
    // arrange
    Class<? extends Annotation> annotation = Path.class;
    // act
    List<Class<?>> classes = AnnotationUtil.getClassesWithAnnotation(Constants.TEST_PACKAGE_NAME, annotation);
    // assert
    assertThat(classes)
        .hasSize(2)
        .contains(ValidationCheckRoute.class, TimeoutRoute.class);

  }
}
