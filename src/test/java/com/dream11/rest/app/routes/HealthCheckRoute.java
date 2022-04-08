package com.dream11.rest.app.routes;

import com.dream11.rest.app.dao.HealthCheckDao;
import com.dream11.rest.app.dto.HealthCheckResponseDTO;
import com.dream11.rest.app.util.CompletableFutureUtils;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
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
    @Inject
    HealthCheckDao healthCheckDao;

    @GET
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponse(content = @Content(schema = @Schema(implementation = HealthCheckResponseDTO.class)))
    public CompletionStage<HealthCheckResponseDTO> healthcheck() {
        return HealthCheckResponseDTO.asyncResponseDtoFromMap(ImmutableMap.of(
                    "aerospike", healthCheckDao.aerospikeHealthCheck()
                ))
                .to(CompletableFutureUtils::fromSingle);
    }
}
