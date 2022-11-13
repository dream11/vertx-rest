package com.dream11.rest.dto;

import com.dream11.rest.annotation.TypeValidationError;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ValidationTestReqDTO {
  @NotNull
  @TypeValidationError(code = "BAD_REQUEST", message = "resourceId must be integer")
  Integer resourceId;
}
