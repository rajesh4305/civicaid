package com.civicaid.dto.response;

import com.civicaid.entity.ComplianceRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ComplianceRecordResponse {
    private Long complianceId;
    private Long entityId;
    private ComplianceRecord.EntityType type;
    private ComplianceRecord.ComplianceResult result;
    private LocalDateTime date;
    private String notes;
    private Long reviewedById;
    private String reviewedByName;
}
