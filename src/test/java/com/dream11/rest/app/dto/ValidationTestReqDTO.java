package com.dream11.rest.app.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ValidationTestReqDTO {
  @NotNull
  Integer resourceId;

  @NotEmpty
  List<Integer> tags;
}
