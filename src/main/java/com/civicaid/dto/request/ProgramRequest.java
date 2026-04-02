package com.civicaid.dto.request;

import com.civicaid.entity.Program;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProgramRequest {
    @NotBlank
    private String title;
    private String description;
    @NotNull
    private LocalDate startDate;
    private LocalDate endDate;
    @NotNull @Positive
    private BigDecimal budget;
    private Program.ProgramStatus status;
}
