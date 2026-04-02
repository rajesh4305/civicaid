package com.civicaid.service;

import com.civicaid.dto.request.EligibilityCheckRequest;
import com.civicaid.dto.response.EligibilityCheckResponse;
import java.util.List;

public interface EligibilityCheckService {
    EligibilityCheckResponse performCheck(Long officerId, EligibilityCheckRequest request);
    List<EligibilityCheckResponse> getChecksByApplication(Long applicationId);
    EligibilityCheckResponse getLatestCheck(Long applicationId);
}
