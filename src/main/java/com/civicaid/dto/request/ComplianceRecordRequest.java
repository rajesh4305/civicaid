package com.civicaid.dto.request;

import com.civicaid.entity.ComplianceRecord;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ComplianceRecordRequest {
    @NotNull
    private Long entityId;
    @NotNull
    private ComplianceRecord.EntityType type;
    @NotNull
    private ComplianceRecord.ComplianceResult result;
    private String notes;
}
