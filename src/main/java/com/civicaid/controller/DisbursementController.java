package com.civicaid.controller;

import com.civicaid.dto.request.DisbursementRequest;
import com.civicaid.dto.response.ApiResponse;
import com.civicaid.dto.response.DisbursementResponse;
import com.civicaid.entity.Disbursement;
import com.civicaid.service.DisbursementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/disbursements")
@RequiredArgsConstructor
public class DisbursementController {

    private final DisbursementService disbursementService;

    @PostMapping
    @PreAuthorize("hasAnyRole('WELFARE_OFFICER','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<DisbursementResponse>> createDisbursement(
            @Valid @RequestBody DisbursementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(disbursementService.createDisbursement(request), "Disbursement created"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DisbursementResponse>> getDisbursementById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(disbursementService.getDisbursementById(id)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('WELFARE_OFFICER','PROGRAM_MANAGER','ADMINISTRATOR','COMPLIANCE_OFFICER','GOVERNMENT_AUDITOR')")
    public ResponseEntity<ApiResponse<Page<DisbursementResponse>>> getAllDisbursements(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) Disbursement.DisbursementStatus status) {
        Page<DisbursementResponse> result = status != null
                ? disbursementService.getDisbursementsByStatus(status, pageable)
                : disbursementService.getAllDisbursements(pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/application/{applicationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<DisbursementResponse>>> getDisbursementsByApplication(
            @PathVariable Long applicationId) {
        return ResponseEntity.ok(ApiResponse.success(
                disbursementService.getDisbursementsByApplication(applicationId)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('WELFARE_OFFICER','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<DisbursementResponse>> updateDisbursementStatus(
            @PathVariable Long id, @RequestParam Disbursement.DisbursementStatus status) {
        return ResponseEntity.ok(ApiResponse.success(
                disbursementService.updateDisbursementStatus(id, status), "Disbursement status updated"));
    }
}
