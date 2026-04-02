package com.civicaid.dto.response;

import com.civicaid.entity.Program;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProgramResponse {
    private Long programId;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal budget;
    private Program.ProgramStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
