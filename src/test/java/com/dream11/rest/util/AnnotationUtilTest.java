package com.dream11.rest.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

import com.dream11.rest.Constants;
import com.dream11.rest.route.TimeoutRoute;
import com.dream11.rest.route.ValidationCheckRoute;
import jakarta.ws.rs.Path;
import java.lang.annotation.Annotation;
import java.util.List;
import org.junit.jupiter.api.Test;

class AnnotationUtilTest {
  @Test
  void testAnnotatedClasses() {
    // arrange
    Class<? extends Annotation> annotation = Path.class;
    // act
    List<Class<?>> classes = AnnotationUtil.getClassesWithAnnotation(Constants.TEST_PACKAGE_NAME, annotation);
    // assert
    assertThat(classes.size(), equalTo(2));
    assertThat(classes, containsInAnyOrder(ValidationCheckRoute.class, TimeoutRoute.class));

  }
}
