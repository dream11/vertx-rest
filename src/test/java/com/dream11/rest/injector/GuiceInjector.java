package com.dream11.rest.injector;

import com.dream11.rest.ClassInjector;
import com.google.inject.Injector;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GuiceInjector implements ClassInjector {
  final Injector injector;

  @Override
  public <T> T getInstance(Class<T> clazz) {
    return this.injector.getInstance(clazz);
  }
}

