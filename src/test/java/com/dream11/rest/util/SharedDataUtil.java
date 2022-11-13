package com.dream11.rest.util;

import io.vertx.core.Vertx;
import io.vertx.core.shareddata.Shareable;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.function.Supplier;

@Slf4j
@UtilityClass
public final class SharedDataUtil {

  private static final String SHARED_DATA_MAP_NAME = "__vertx.sharedDataUtils";
  private static final String SHARED_DATA_CLASS_PREFIX = "__class.";
  private static final String SHARED_DATA_DEFAULT_KEY = "__default.";


  /**
   * Returns a singleton shared object across vert.x instance
   * Note: It is your responsibility to ensure T returned by supplier is thread-safe
   */
  public <T> T getOrCreate(Vertx vertx, String name, Supplier<T> supplier) {
    val singletons = vertx.sharedData().getLocalMap(SHARED_DATA_MAP_NAME);
    // LocalMap is internally backed by a ConcurrentMap
    return ((ThreadSafe<T>) singletons.computeIfAbsent(name, k -> new ThreadSafe(supplier.get()))).getObject();
  }

  /**
   * Helper wrapper on getOrCreate to setInstance.
   * Note: Doesn't reset the instance if already exists
   */
  public <T> T setInstance(Vertx vertx, T instance) {
    return getOrCreate(vertx, SHARED_DATA_CLASS_PREFIX + SHARED_DATA_DEFAULT_KEY + instance.getClass().getName(), () -> instance);
  }

  /**
   * Helper wrapper on getOrCreate to getInstance.
   */
  public <T> T getInstance(Vertx vertx, Class<T> clazz) {
    return getOrCreate(vertx, SHARED_DATA_CLASS_PREFIX + SHARED_DATA_DEFAULT_KEY + clazz.getName(), () -> {
      throw new RuntimeException("Cannot find default instance of " + clazz.getName());
    });
  }

  static class ThreadSafe<T> implements Shareable {
    @Getter
    final T object;

    public ThreadSafe(T object) {
      this.object = object;
    }
  }
}