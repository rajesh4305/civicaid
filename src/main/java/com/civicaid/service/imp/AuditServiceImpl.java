package com.civicaid.service.impl;

import com.civicaid.dto.request.AuditRequest;
import com.civicaid.dto.response.AuditResponse;
import com.civicaid.entity.Audit;
import com.civicaid.entity.User;
import com.civicaid.exception.ResourceNotFoundException;
import com.civicaid.repository.AuditRepository;
import com.civicaid.repository.UserRepository;
import com.civicaid.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AuditResponse createAudit(Long officerId, AuditRequest request) {
        User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", officerId));

        Audit audit = Audit.builder()
                .officer(officer)
                .scope(request.getScope())
                .findings(request.getFindings())
                .status(request.getStatus() != null ? request.getStatus() : Audit.AuditStatus.IN_PROGRESS)
                .build();

        return mapToResponse(auditRepository.save(audit));
    }

    @Override
    public AuditResponse getAuditById(Long id) {
        return mapToResponse(auditRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Audit", id)));
    }

    @Override
    public Page<AuditResponse> getAllAudits(Pageable pageable) {
        return auditRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public Page<AuditResponse> getAuditsByOfficer(Long officerId, Pageable pageable) {
        return auditRepository.findByOfficer_UserId(officerId, pageable).map(this::mapToResponse);
    }

    @Override
    public Page<AuditResponse> getAuditsByStatus(Audit.AuditStatus status, Pageable pageable) {
        return auditRepository.findByStatus(status, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional
    public AuditResponse updateAudit(Long id, AuditRequest request) {
        Audit audit = auditRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Audit", id));
        audit.setScope(request.getScope());
        audit.setFindings(request.getFindings());
        if (request.getStatus() != null) audit.setStatus(request.getStatus());
        return mapToResponse(auditRepository.save(audit));
    }

    private AuditResponse mapToResponse(Audit a) {
        return AuditResponse.builder()
                .auditId(a.getAuditId())
                .officerId(a.getOfficer().getUserId())
                .officerName(a.getOfficer().getName())
                .scope(a.getScope())
                .findings(a.getFindings())
                .date(a.getDate())
                .status(a.getStatus())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}
