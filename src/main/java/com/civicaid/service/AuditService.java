package com.civicaid.service;

import com.civicaid.dto.request.AuditRequest;
import com.civicaid.dto.response.AuditResponse;
import com.civicaid.entity.Audit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditService {
    AuditResponse createAudit(Long officerId, AuditRequest request);
    AuditResponse getAuditById(Long id);
    Page<AuditResponse> getAllAudits(Pageable pageable);
    Page<AuditResponse> getAuditsByOfficer(Long officerId, Pageable pageable);
    Page<AuditResponse> getAuditsByStatus(Audit.AuditStatus status, Pageable pageable);
    AuditResponse updateAudit(Long id, AuditRequest request);
}
