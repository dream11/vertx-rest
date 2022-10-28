package com.dream11.rest.app.routes;

import com.dream11.rest.annotation.Timeout;
import com.dream11.rest.app.dao.HealthCheckDao;
import com.dream11.rest.app.dto.HealthCheckResponseDTO;
import com.dream11.rest.util.CompletableFutureUtils;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
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

@Slf4j
@Path("/healthcheck")
@Timeout(value = 10000)
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
