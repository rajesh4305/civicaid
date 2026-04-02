package com.civicaid.service.impl;

import com.civicaid.dto.request.ComplianceRecordRequest;
import com.civicaid.dto.response.ComplianceRecordResponse;
import com.civicaid.entity.ComplianceRecord;
import com.civicaid.entity.User;
import com.civicaid.exception.ResourceNotFoundException;
import com.civicaid.repository.ComplianceRecordRepository;
import com.civicaid.repository.UserRepository;
import com.civicaid.service.ComplianceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplianceServiceImpl implements ComplianceService {

    private final ComplianceRecordRepository complianceRecordRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ComplianceRecordResponse createRecord(Long reviewerId, ComplianceRecordRequest request) {
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", reviewerId));

        ComplianceRecord record = ComplianceRecord.builder()
                .entityId(request.getEntityId())
                .type(request.getType())
                .result(request.getResult())
                .notes(request.getNotes())
                .reviewedBy(reviewer)
                .build();

        return mapToResponse(complianceRecordRepository.save(record));
    }

    @Override
    public ComplianceRecordResponse getRecordById(Long id) {
        return mapToResponse(complianceRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compliance Record", id)));
    }

    @Override
    public List<ComplianceRecordResponse> getRecordsByEntity(Long entityId, ComplianceRecord.EntityType type) {
        return complianceRecordRepository.findByEntityIdAndType(entityId, type)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    public Page<ComplianceRecordResponse> getAllRecords(Pageable pageable) {
        return complianceRecordRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public Page<ComplianceRecordResponse> getRecordsByResult(ComplianceRecord.ComplianceResult result, Pageable pageable) {
        return complianceRecordRepository.findByResult(result, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional
    public ComplianceRecordResponse updateRecord(Long id, ComplianceRecordRequest request) {
        ComplianceRecord record = complianceRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compliance Record", id));
        record.setResult(request.getResult());
        record.setNotes(request.getNotes());
        return mapToResponse(complianceRecordRepository.save(record));
    }

    private ComplianceRecordResponse mapToResponse(ComplianceRecord cr) {
        return ComplianceRecordResponse.builder()
                .complianceId(cr.getComplianceId())
                .entityId(cr.getEntityId())
                .type(cr.getType())
                .result(cr.getResult())
                .date(cr.getDate())
                .notes(cr.getNotes())
                .reviewedById(cr.getReviewedBy() != null ? cr.getReviewedBy().getUserId() : null)
                .reviewedByName(cr.getReviewedBy() != null ? cr.getReviewedBy().getName() : null)
                .build();
    }
}
