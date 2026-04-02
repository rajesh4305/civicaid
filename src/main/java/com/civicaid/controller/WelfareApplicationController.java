package com.civicaid.controller;

import com.civicaid.dto.request.WelfareApplicationRequest;
import com.civicaid.dto.response.ApiResponse;
import com.civicaid.dto.response.WelfareApplicationResponse;
import com.civicaid.entity.User;
import com.civicaid.entity.WelfareApplication;
import com.civicaid.exception.ResourceNotFoundException;
import com.civicaid.repository.CitizenRepository;
import com.civicaid.repository.UserRepository;
import com.civicaid.service.WelfareApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class WelfareApplicationController {

    private final WelfareApplicationService applicationService;
    private final UserRepository userRepository;
    private final CitizenRepository citizenRepository;

    @PostMapping
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<ApiResponse<WelfareApplicationResponse>> submitApplication(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody WelfareApplicationRequest request) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        var citizen = citizenRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Citizen profile not found"));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        applicationService.submitApplication(citizen.getCitizenId(), request),
                        "Application submitted successfully"));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<ApiResponse<Page<WelfareApplicationResponse>>> getMyApplications(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        var citizen = citizenRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Citizen profile not found"));
        return ResponseEntity.ok(ApiResponse.success(
                applicationService.getApplicationsByCitizen(citizen.getCitizenId(), pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<WelfareApplicationResponse>> getApplicationById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(applicationService.getApplicationById(id)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('WELFARE_OFFICER','PROGRAM_MANAGER','ADMINISTRATOR','COMPLIANCE_OFFICER','GOVERNMENT_AUDITOR')")
    public ResponseEntity<ApiResponse<Page<WelfareApplicationResponse>>> getAllApplications(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) WelfareApplication.ApplicationStatus status,
            @RequestParam(required = false) Long programId) {
        Page<WelfareApplicationResponse> result;
        if (status != null) result = applicationService.getApplicationsByStatus(status, pageable);
        else if (programId != null) result = applicationService.getApplicationsByProgram(programId, pageable);
        else result = applicationService.getAllApplications(pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('WELFARE_OFFICER','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<WelfareApplicationResponse>> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam WelfareApplication.ApplicationStatus status,
            @RequestParam(required = false) String remarks) {
        return ResponseEntity.ok(ApiResponse.success(
                applicationService.updateApplicationStatus(id, status, remarks),
                "Application status updated"));
    }

    @PatchMapping("/{id}/withdraw")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<ApiResponse<Void>> withdrawApplication(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        var citizen = citizenRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Citizen profile not found"));
        applicationService.withdrawApplication(id, citizen.getCitizenId());
        return ResponseEntity.ok(ApiResponse.success(null, "Application withdrawn"));
    }
}
