package com.civicaid.dto.response;

import com.civicaid.entity.EligibilityCheck;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EligibilityCheckResponse {
    private Long checkId;
    private Long applicationId;
    private Long officerId;
    private String officerName;
    private EligibilityCheck.CheckResult result;
    private LocalDateTime date;
    private String notes;
}
