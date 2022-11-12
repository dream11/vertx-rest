package com.dream11.rest.app.util;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class CollectionUtils {
  public static <E, R, T> List<T> mapToList(BiFunction<E, R, T> mapper, Map<E, R> map) {
    return map.entrySet().stream().map(entry -> mapper.apply(entry.getKey(), entry.getValue())).collect(Collectors.toList());
  }
}
