package com.dream11.rest.app.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ValidationTestReqDTO {
    @NotNull
    Integer resourceId;

    @NotEmpty
    List<Integer> tags;
}
