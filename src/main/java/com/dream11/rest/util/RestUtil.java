package com.dream11.rest.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import io.vertx.core.json.jackson.DatabindCodec;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

@Slf4j
public class RestUtil {

  private static final List<Class> collectionClasses = Arrays.asList(
      List.class,
      Set.class,
      ArrayList.class,
      HashSet.class,
      LinkedHashSet.class,
      LinkedList.class
  );
  private static Reflections ref;

  public static List<Class<?>> annotatedClasses(String packageName, Class<? extends Annotation> annotation) {
    List<Class<?>> annotatedClasses = new ArrayList<>();
    try {
      setRef(packageName);
      annotatedClasses = new ArrayList<>(ref.getTypesAnnotatedWith(annotation));
    } catch (Exception e) {
      log.error("Failed to get classes with annotation {}", annotation, e);
    }
    return annotatedClasses;
  }

  private static synchronized void setRef(String packageName) {
    if (ref == null) {
      ref = new Reflections(packageName);
    }
  }

  public static String getString(Object object) throws JsonProcessingException {
    ObjectMapper objectMapper = DatabindCodec.mapper();
    String str;
    if (object instanceof String) {
      str = (String) object;
    } else if (object instanceof JsonObject) {
      str = String.valueOf(object);
    } else if (collectionClasses.contains(object.getClass())) {
      str = new JsonArray(new ArrayList((Collection) object)).toString();
    } else {
      str = objectMapper.writeValueAsString(object);
    }
    return str;
  }
}
