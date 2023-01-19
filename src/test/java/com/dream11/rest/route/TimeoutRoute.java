package com.dream11.rest.route;

import com.dream11.rest.annotation.Timeout;
import com.dream11.rest.Constants;
import io.reactivex.rxjava3.core.Single;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

@Slf4j
@Path(Constants.TIMEOUT_ROUTE_PATH)
@Timeout(value = 10, httpStatusCode = 503)
public class TimeoutRoute {

  @GET
  @Path("/class")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  @ApiResponse(content = @Content(schema = @Schema(implementation = String.class)))
  public CompletionStage<String> timeout() {
    return Single.just("1")
        .delay(20, TimeUnit.MILLISECONDS)
        .toCompletionStage();
  }

  @GET
  @Path("/method")
  @Timeout(value = 10, httpStatusCode = 503)
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  @ApiResponse(content = @Content(schema = @Schema(implementation = String.class)))
  public CompletionStage<String> timeoutMethod() {
    return Single.just("1")
        .delay(20, TimeUnit.MILLISECONDS)
        .toCompletionStage();
  }
}
