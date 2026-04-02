package com.civicaid.dto.response;

import com.civicaid.entity.Disbursement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DisbursementResponse {
    private Long disbursementId;
    private Long applicationId;
    private String citizenName;
    private String programTitle;
    private BigDecimal amount;
    private LocalDateTime date;
    private Disbursement.DisbursementStatus status;
    private String remarks;
    private LocalDateTime updatedAt;
}
