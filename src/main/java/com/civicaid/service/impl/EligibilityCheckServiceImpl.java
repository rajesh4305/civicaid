package com.civicaid.service.impl;

import com.civicaid.dto.request.EligibilityCheckRequest;
import com.civicaid.dto.response.EligibilityCheckResponse;
import com.civicaid.entity.*;
import com.civicaid.exception.ResourceNotFoundException;
import com.civicaid.repository.*;
import com.civicaid.service.EligibilityCheckService;
import com.civicaid.service.NotificationService;
import com.civicaid.service.WelfareApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EligibilityCheckServiceImpl implements EligibilityCheckService {

    private final EligibilityCheckRepository eligibilityCheckRepository;
    private final WelfareApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public EligibilityCheckResponse performCheck(Long officerId, EligibilityCheckRequest request) {
        WelfareApplication application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application", request.getApplicationId()));

        User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException("Officer", officerId));

        EligibilityCheck check = EligibilityCheck.builder()
                .application(application)
                .officer(officer)
                .result(request.getResult())
                .notes(request.getNotes())
                .build();

        check = eligibilityCheckRepository.save(check);

        // Update application status based on result
        WelfareApplication.ApplicationStatus newStatus = switch (request.getResult()) {
            case ELIGIBLE -> WelfareApplication.ApplicationStatus.ELIGIBLE;
            case INELIGIBLE -> WelfareApplication.ApplicationStatus.INELIGIBLE;
            default -> WelfareApplication.ApplicationStatus.UNDER_REVIEW;
        };
        application.setStatus(newStatus);
        applicationRepository.save(application);

        // Notify citizen
        notificationService.sendNotification(
                application.getCitizen().getUser().getUserId(),
                application.getApplicationId(),
                "Eligibility check completed for your application. Result: " + request.getResult().name(),
                Notification.NotificationCategory.APPLICATION
        );

        return mapToResponse(check);
    }

    @Override
    public List<EligibilityCheckResponse> getChecksByApplication(Long applicationId) {
        return eligibilityCheckRepository.findByApplication_ApplicationId(applicationId)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    public EligibilityCheckResponse getLatestCheck(Long applicationId) {
        EligibilityCheck check = eligibilityCheckRepository
                .findTopByApplication_ApplicationIdOrderByDateDesc(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("No eligibility check found for application: " + applicationId));
        return mapToResponse(check);
    }

    private EligibilityCheckResponse mapToResponse(EligibilityCheck ec) {
        return EligibilityCheckResponse.builder()
                .checkId(ec.getCheckId())
                .applicationId(ec.getApplication().getApplicationId())
                .officerId(ec.getOfficer().getUserId())
                .officerName(ec.getOfficer().getName())
                .result(ec.getResult())
                .date(ec.getDate())
                .notes(ec.getNotes())
                .build();
    }
}
