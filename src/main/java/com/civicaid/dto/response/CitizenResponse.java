package com.civicaid.dto.response;

import com.civicaid.entity.Citizen;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CitizenResponse {
    private Long citizenId;
    private String name;
    private LocalDate dob;
    private Citizen.Gender gender;
    private String address;
    private String contactInfo;
    private Citizen.CitizenStatus status;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
