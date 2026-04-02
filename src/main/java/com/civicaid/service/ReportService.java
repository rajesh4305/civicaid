package com.civicaid.service;

import com.civicaid.dto.response.ApiResponse;
import java.util.Map;

public interface ReportService {
    Map<String, Object> getDashboardStats();
    Map<String, Object> getApplicationStats();
    Map<String, Object> getDisbursementStats();
    Map<String, Object> getComplianceStats();
    Map<String, Object> getProgramStats(Long programId);
}
