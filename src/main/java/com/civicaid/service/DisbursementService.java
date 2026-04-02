package com.civicaid.service;

import com.civicaid.dto.request.DisbursementRequest;
import com.civicaid.dto.response.DisbursementResponse;
import com.civicaid.entity.Disbursement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface DisbursementService {
    DisbursementResponse createDisbursement(DisbursementRequest request);
    DisbursementResponse getDisbursementById(Long id);
    List<DisbursementResponse> getDisbursementsByApplication(Long applicationId);
    Page<DisbursementResponse> getDisbursementsByStatus(Disbursement.DisbursementStatus status, Pageable pageable);
    Page<DisbursementResponse> getAllDisbursements(Pageable pageable);
    DisbursementResponse updateDisbursementStatus(Long id, Disbursement.DisbursementStatus status);
}
