package com.dream11.rest.app.routes;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.CompletionStage;

@Slf4j
@Path("/healthcheck")
public class HealthCheckRoute {
//    @Inject
//    HealthCheckDao healthCheckDao;

    @GET
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponse(content = @Content(schema = @Schema(implementation = String.class)))
    public String healthcheck() {
       return "hello";
    }
}
