package com.civicaid.controller;

import com.civicaid.dto.request.ComplianceRecordRequest;
import com.civicaid.dto.response.ApiResponse;
import com.civicaid.dto.response.ComplianceRecordResponse;
import com.civicaid.entity.ComplianceRecord;
import com.civicaid.entity.User;
import com.civicaid.exception.ResourceNotFoundException;
import com.civicaid.repository.UserRepository;
import com.civicaid.service.ComplianceService;
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

import java.util.List;

@RestController
@RequestMapping("/compliance")
@RequiredArgsConstructor
public class ComplianceController {

    private final ComplianceService complianceService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<ComplianceRecordResponse>> createRecord(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ComplianceRecordRequest request) {
        User reviewer = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(complianceService.createRecord(reviewer.getUserId(), request), "Compliance record created"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER','ADMINISTRATOR','GOVERNMENT_AUDITOR')")
    public ResponseEntity<ApiResponse<ComplianceRecordResponse>> getRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(complianceService.getRecordById(id)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER','ADMINISTRATOR','GOVERNMENT_AUDITOR')")
    public ResponseEntity<ApiResponse<Page<ComplianceRecordResponse>>> getAllRecords(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) ComplianceRecord.ComplianceResult result) {
        Page<ComplianceRecordResponse> page = result != null
                ? complianceService.getRecordsByResult(result, pageable)
                : complianceService.getAllRecords(pageable);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @GetMapping("/entity/{entityId}")
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER','ADMINISTRATOR','GOVERNMENT_AUDITOR')")
    public ResponseEntity<ApiResponse<List<ComplianceRecordResponse>>> getRecordsByEntity(
            @PathVariable Long entityId,
            @RequestParam ComplianceRecord.EntityType type) {
        return ResponseEntity.ok(ApiResponse.success(complianceService.getRecordsByEntity(entityId, type)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<ComplianceRecordResponse>> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody ComplianceRecordRequest request) {
        return ResponseEntity.ok(ApiResponse.success(complianceService.updateRecord(id, request), "Record updated"));
    }
}
