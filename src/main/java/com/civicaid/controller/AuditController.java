package com.civicaid.controller;

import com.civicaid.dto.request.AuditRequest;
import com.civicaid.dto.response.ApiResponse;
import com.civicaid.dto.response.AuditResponse;
import com.civicaid.entity.Audit;
import com.civicaid.entity.User;
import com.civicaid.exception.ResourceNotFoundException;
import com.civicaid.repository.UserRepository;
import com.civicaid.service.AuditService;
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
@RequestMapping("/audits")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<AuditResponse>> createAudit(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AuditRequest request) {
        User officer = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(auditService.createAudit(officer.getUserId(), request), "Audit created"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER','ADMINISTRATOR','GOVERNMENT_AUDITOR')")
    public ResponseEntity<ApiResponse<AuditResponse>> getAuditById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(auditService.getAuditById(id)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER','ADMINISTRATOR','GOVERNMENT_AUDITOR')")
    public ResponseEntity<ApiResponse<Page<AuditResponse>>> getAllAudits(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) Audit.AuditStatus status) {
        Page<AuditResponse> result = status != null
                ? auditService.getAuditsByStatus(status, pageable)
                : auditService.getAllAudits(pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<Page<AuditResponse>>> getMyAudits(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        User officer = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(ApiResponse.success(auditService.getAuditsByOfficer(officer.getUserId(), pageable)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<AuditResponse>> updateAudit(
            @PathVariable Long id,
            @Valid @RequestBody AuditRequest request) {
        return ResponseEntity.ok(ApiResponse.success(auditService.updateAudit(id, request), "Audit updated"));
    }
}
