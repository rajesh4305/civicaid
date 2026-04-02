package com.civicaid.dto.request;

import com.civicaid.entity.WelfareApplication;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WelfareApplicationRequest {
    @NotNull
    private Long programId;
    private Long schemeId;
    private String remarks;
}
