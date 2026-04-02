package com.civicaid.dto.request;

import com.civicaid.entity.Scheme;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SchemeRequest {
    @NotNull
    private Long programId;
    @NotBlank
    private String title;
    private String description;
    private String eligibilityCriteria;
    private Scheme.SchemeStatus status;
}
