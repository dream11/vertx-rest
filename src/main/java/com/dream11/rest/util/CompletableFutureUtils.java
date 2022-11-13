package com.dream11.rest.util;

import io.reactivex.Single;
import lombok.experimental.UtilityClass;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;

@UtilityClass
public class CompletableFutureUtils {
  public static <T> VertxCompletableFuture<T> fromSingle(Single<T> single) {

    VertxCompletableFuture<T> vertxCompletableFuture = new VertxCompletableFuture<>();
    single.subscribe(vertxCompletableFuture::complete, vertxCompletableFuture::completeExceptionally);
    return vertxCompletableFuture;
  }
}
