
package com.dream11.rest.verticle;

import com.dream11.rest.AbstractRestVerticle;
import com.dream11.rest.ClassInjector;
import com.dream11.rest.Constants;
import com.dream11.rest.injector.GuiceInjector;
import com.dream11.rest.util.SharedDataUtil;
import io.vertx.core.http.HttpServerOptions;

public class RestVerticle extends AbstractRestVerticle {

  public RestVerticle() {
    super(Constants.TEST_PACKAGE_NAME, new HttpServerOptions().setPort(8080));
  }

  @Override
  protected ClassInjector getInjector() {
    return SharedDataUtil.getInstance(this.vertx.getDelegate(), GuiceInjector.class);
  }
}
