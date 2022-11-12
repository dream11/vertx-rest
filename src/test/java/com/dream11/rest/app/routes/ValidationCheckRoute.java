package com.dream11.rest.app.routes;

import com.dream11.rest.annotation.Timeout;
import com.dream11.rest.app.Constants;
import com.dream11.rest.app.dto.ValidationTestReqDTO;
import com.dream11.rest.util.CompletableFutureUtils;
import io.reactivex.Single;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.concurrent.CompletionStage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path(Constants.VALIDATION_ROUTE_PATH)
@Timeout(value = 10000)
public class ValidationCheckRoute {
  @GET
  @Path("{testResourceId}")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  @ApiResponse(content = @Content(schema = @Schema(implementation = String.class)))
  public CompletionStage<String> getValidationTest(@NotNull @HeaderParam("testHeader") Integer testHeader,
                                                   @NotNull @Positive @PathParam("testResourceId") Integer testResourceId,
                                                   @NotBlank @QueryParam("testFilter") String testFilter
  ) {
    return Single.just(
            String.format("Validation Successful! testHeader: %s, testResourceId: %s, testFilter: %s", testHeader, testResourceId, testFilter))
        .to(CompletableFutureUtils::fromSingle);
  }

  @POST
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  @ApiResponse(content = @Content(schema = @Schema(implementation = String.class)))
  public CompletionStage<String> postValidationTest(@Valid ValidationTestReqDTO reqDTO) {
    return Single.just(String.format("Validation Successful! reqBody: %s", reqDTO.toString()))
        .to(CompletableFutureUtils::fromSingle);
  }
}
