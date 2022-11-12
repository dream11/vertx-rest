package com.dream11.rest.app.dto;

import com.dream11.rest.app.util.CollectionUtils;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;

@Data
public class HealthCheckResponseDTO {
  List<Check> checks;

  HealthCheckResponseDTO(List<Check> checks) {
    this.checks = checks;
  }

  public static Single<HealthCheckResponseDTO> asyncResponseDtoFromMap(Map<String, Single<JsonObject>> checks) {
    return Single.zip(CollectionUtils.mapToList(
            (type, single) -> single.map(result -> new Check(type, result))
                .onErrorReturn(err -> new Check(type, err)),
            checks),
        checksWithType -> new HealthCheckResponseDTO(Arrays.asList(Arrays.copyOf(checksWithType, checksWithType.length, Check[].class))));
  }

  @Override
  public String toString() {
    return new JsonArray(checks).toString();
  }

  @Getter
  @ToString
  @RequiredArgsConstructor()
  enum Status {
    UP("UP"),
    DOWN("DOWN");

    private final String name;
  }

  @Value
  static class Check {
    String error;
    JsonObject response;
    Status status;
    String type;

    Check(String type, Throwable error) {
      this.type = type;
      this.response = null;
      this.error = error.toString();
      this.status = Status.DOWN;
    }

    Check(String type, JsonObject response) {
      this.type = type;
      this.response = response;
      this.error = null;
      this.status = Status.UP;
    }
  }
}
