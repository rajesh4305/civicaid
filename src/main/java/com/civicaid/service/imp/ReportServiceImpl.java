package com.civicaid.service.impl;

import com.civicaid.entity.Audit;
import com.civicaid.entity.ComplianceRecord;
import com.civicaid.entity.Disbursement;
import com.civicaid.entity.WelfareApplication;
import com.civicaid.exception.ResourceNotFoundException;
import com.civicaid.repository.*;
import com.civicaid.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final WelfareApplicationRepository applicationRepository;
    private final DisbursementRepository disbursementRepository;
    private final CitizenRepository citizenRepository;
    private final ProgramRepository programRepository;
    private final ComplianceRecordRepository complianceRecordRepository;
    private final AuditRepository auditRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        // Citizen stats
        stats.put("totalCitizens", citizenRepository.count());
        stats.put("verifiedCitizens", citizenRepository.countByStatus(com.civicaid.entity.Citizen.CitizenStatus.VERIFIED));
        stats.put("pendingCitizens", citizenRepository.countByStatus(com.civicaid.entity.Citizen.CitizenStatus.PENDING));

        // Application stats
        stats.put("totalApplications", applicationRepository.count());
        stats.put("submittedApplications", applicationRepository.countByStatus(WelfareApplication.ApplicationStatus.SUBMITTED));
        stats.put("approvedApplications", applicationRepository.countByStatus(WelfareApplication.ApplicationStatus.APPROVED));
        stats.put("rejectedApplications", applicationRepository.countByStatus(WelfareApplication.ApplicationStatus.REJECTED));
        stats.put("disbursedApplications", applicationRepository.countByStatus(WelfareApplication.ApplicationStatus.DISBURSED));

        // Program stats
        stats.put("totalPrograms", programRepository.count());
        stats.put("activePrograms", programRepository.countByStatus(com.civicaid.entity.Program.ProgramStatus.ACTIVE));
        BigDecimal totalBudget = programRepository.sumActiveProgramBudgets();
        stats.put("totalActiveBudget", totalBudget != null ? totalBudget : BigDecimal.ZERO);

        // Disbursement stats
        BigDecimal totalDisbursed = disbursementRepository.sumCompletedDisbursements();
        stats.put("totalDisbursed", totalDisbursed != null ? totalDisbursed : BigDecimal.ZERO);
        stats.put("pendingDisbursements", disbursementRepository.countByStatus(Disbursement.DisbursementStatus.PENDING));
        stats.put("completedDisbursements", disbursementRepository.countByStatus(Disbursement.DisbursementStatus.COMPLETED));

        // Compliance stats
        stats.put("compliantRecords", complianceRecordRepository.countByResult(ComplianceRecord.ComplianceResult.COMPLIANT));
        stats.put("nonCompliantRecords", complianceRecordRepository.countByResult(ComplianceRecord.ComplianceResult.NON_COMPLIANT));

        // Audit stats
        stats.put("openAudits", auditRepository.countByStatus(Audit.AuditStatus.IN_PROGRESS));
        stats.put("completedAudits", auditRepository.countByStatus(Audit.AuditStatus.COMPLETED));

        return stats;
    }

    @Override
    public Map<String, Object> getApplicationStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total", applicationRepository.count());
        for (WelfareApplication.ApplicationStatus s : WelfareApplication.ApplicationStatus.values()) {
            stats.put(s.name().toLowerCase(), applicationRepository.countByStatus(s));
        }
        long total = applicationRepository.count();
        long approved = applicationRepository.countByStatus(WelfareApplication.ApplicationStatus.APPROVED);
        stats.put("approvalRate", total > 0 ? String.format("%.2f%%", (approved * 100.0 / total)) : "0.00%");
        return stats;
    }

    @Override
    public Map<String, Object> getDisbursementStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total", disbursementRepository.count());
        for (Disbursement.DisbursementStatus s : Disbursement.DisbursementStatus.values()) {
            stats.put(s.name().toLowerCase(), disbursementRepository.countByStatus(s));
        }
        BigDecimal totalDisbursed = disbursementRepository.sumCompletedDisbursements();
        stats.put("totalAmountDisbursed", totalDisbursed != null ? totalDisbursed : BigDecimal.ZERO);

        BigDecimal successfulPayments = paymentRepository.sumSuccessfulPayments();
        stats.put("totalPaymentsProcessed", successfulPayments != null ? successfulPayments : BigDecimal.ZERO);
        return stats;
    }

    @Override
    public Map<String, Object> getComplianceStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalRecords", complianceRecordRepository.count());
        for (ComplianceRecord.ComplianceResult r : ComplianceRecord.ComplianceResult.values()) {
            stats.put(r.name().toLowerCase(), complianceRecordRepository.countByResult(r));
        }
        stats.put("totalAudits", auditRepository.count());
        for (Audit.AuditStatus s : Audit.AuditStatus.values()) {
            stats.put("audits_" + s.name().toLowerCase(), auditRepository.countByStatus(s));
        }
        long total = complianceRecordRepository.count();
        long compliant = complianceRecordRepository.countByResult(ComplianceRecord.ComplianceResult.COMPLIANT);
        stats.put("complianceRate", total > 0 ? String.format("%.2f%%", (compliant * 100.0 / total)) : "0.00%");
        return stats;
    }

    @Override
    public Map<String, Object> getProgramStats(Long programId) {
        programRepository.findById(programId)
                .orElseThrow(() -> new ResourceNotFoundException("Program", programId));

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("programId", programId);
        stats.put("totalApplications", applicationRepository.findByProgram_ProgramId(programId,
                org.springframework.data.domain.Pageable.unpaged()).getTotalElements());

        for (WelfareApplication.ApplicationStatus s : WelfareApplication.ApplicationStatus.values()) {
            long count = applicationRepository.findByCitizenAndStatus(null, s) != null ? 0 : 0;
            // Use count query per program
        }

        BigDecimal disbursedForProgram = disbursementRepository.sumDisbursedAmountByProgram(programId);
        stats.put("totalDisbursedAmount", disbursedForProgram != null ? disbursedForProgram : BigDecimal.ZERO);

        return stats;
    }
}
