package com.civicaid.dto.response;

import com.civicaid.entity.WelfareApplication;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class WelfareApplicationResponse {
    private Long applicationId;
    private Long citizenId;
    private String citizenName;
    private Long programId;
    private String programTitle;
    private Long schemeId;
    private String schemeTitle;
    private LocalDateTime submittedDate;
    private WelfareApplication.ApplicationStatus status;
    private String remarks;
    private LocalDateTime updatedAt;
}
