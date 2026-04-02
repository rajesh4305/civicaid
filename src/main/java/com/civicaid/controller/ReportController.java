package com.civicaid.controller;

import com.civicaid.dto.response.ApiResponse;
import com.civicaid.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('PROGRAM_MANAGER','ADMINISTRATOR','GOVERNMENT_AUDITOR','COMPLIANCE_OFFICER')") //complaince is not working
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getDashboardStats(), "Dashboard statistics"));
    }

    @GetMapping("/applications")
    @PreAuthorize("hasAnyRole('WELFARE_OFFICER','PROGRAM_MANAGER','ADMINISTRATOR','GOVERNMENT_AUDITOR')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getApplicationStats() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getApplicationStats(), "Application statistics"));
    }

    @GetMapping("/disbursements")
    @PreAuthorize("hasAnyRole('PROGRAM_MANAGER','ADMINISTRATOR','GOVERNMENT_AUDITOR')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDisbursementStats() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getDisbursementStats(), "Disbursement statistics"));
    }

    @GetMapping("/compliance")
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER','ADMINISTRATOR','GOVERNMENT_AUDITOR')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getComplianceStats() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getComplianceStats(), "Compliance statistics"));
    }

    @GetMapping("/programs/{programId}")
    @PreAuthorize("hasAnyRole('PROGRAM_MANAGER','ADMINISTRATOR','GOVERNMENT_AUDITOR')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProgramStats(@PathVariable Long programId) {
        return ResponseEntity.ok(ApiResponse.success(reportService.getProgramStats(programId), "Program statistics"));
    }
}
