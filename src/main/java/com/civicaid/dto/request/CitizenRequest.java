package com.civicaid.dto.request;

import com.civicaid.entity.Citizen;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CitizenRequest {
    @NotBlank
    private String name;
    @NotNull @Past
    private LocalDate dob;
    private Citizen.Gender gender;
    private String address;
    private String contactInfo;
}
