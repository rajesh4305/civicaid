package com.civicaid.dto.response;

import com.civicaid.entity.Audit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuditResponse {
    private Long auditId;
    private Long officerId;
    private String officerName;
    private String scope;
    private String findings;
    private LocalDateTime date;
    private Audit.AuditStatus status;
    private LocalDateTime updatedAt;
}
