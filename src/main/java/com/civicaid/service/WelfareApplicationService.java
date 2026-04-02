package com.civicaid.service;

import com.civicaid.dto.request.WelfareApplicationRequest;
import com.civicaid.dto.response.WelfareApplicationResponse;
import com.civicaid.entity.WelfareApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WelfareApplicationService {
    WelfareApplicationResponse submitApplication(Long citizenId, WelfareApplicationRequest request);
    WelfareApplicationResponse getApplicationById(Long id);
    Page<WelfareApplicationResponse> getApplicationsByCitizen(Long citizenId, Pageable pageable);
    Page<WelfareApplicationResponse> getApplicationsByProgram(Long programId, Pageable pageable);
    Page<WelfareApplicationResponse> getApplicationsByStatus(WelfareApplication.ApplicationStatus status, Pageable pageable);
    Page<WelfareApplicationResponse> getAllApplications(Pageable pageable);
    WelfareApplicationResponse updateApplicationStatus(Long id, WelfareApplication.ApplicationStatus status, String remarks);
    void withdrawApplication(Long id, Long citizenId);
}
