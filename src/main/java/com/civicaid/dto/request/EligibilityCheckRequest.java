package com.civicaid.dto.request;

import com.civicaid.entity.EligibilityCheck;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EligibilityCheckRequest {
    @NotNull
    private Long applicationId;
    @NotNull
    private EligibilityCheck.CheckResult result;
    private String notes;
}
