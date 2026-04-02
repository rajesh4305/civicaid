
package com.civicaid.service.impl;

import com.civicaid.dto.request.WelfareApplicationRequest;
import com.civicaid.dto.response.WelfareApplicationResponse;
import com.civicaid.entity.*;
import com.civicaid.exception.BusinessException;
import com.civicaid.exception.ResourceNotFoundException;
import com.civicaid.repository.*;
import com.civicaid.service.AuditLogService;
import com.civicaid.service.NotificationService;
import com.civicaid.service.WelfareApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WelfareApplicationServiceImpl implements WelfareApplicationService {

    private final WelfareApplicationRepository applicationRepository;
    private final CitizenRepository citizenRepository;
    private final ProgramRepository programRepository;
    private final SchemeRepository schemeRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;
    private final com.civicaid.repository.UserRepository userRepository;

    private Long currentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).map(User::getUserId).orElse(null);
    }

    @Override
    @Transactional
    public WelfareApplicationResponse submitApplication(Long citizenId, WelfareApplicationRequest request) {
        Citizen citizen = citizenRepository.findById(citizenId)
                .orElseThrow(() -> new ResourceNotFoundException("Citizen", citizenId));

        if (citizen.getStatus() != Citizen.CitizenStatus.VERIFIED) {
            throw new BusinessException("Citizen profile must be verified before applying for welfare programs");
        }

        if (applicationRepository.existsByCitizen_CitizenIdAndProgram_ProgramId(citizenId, request.getProgramId())) {
            throw new BusinessException("Application already submitted for this program");
        }

        Program program = programRepository.findById(request.getProgramId())
                .orElseThrow(() -> new ResourceNotFoundException("Program", request.getProgramId()));

        if (program.getStatus() != Program.ProgramStatus.ACTIVE) {
            throw new BusinessException("Program is not currently accepting applications");
        }

        Scheme scheme = null;
        if (request.getSchemeId() != null) {
            scheme = schemeRepository.findById(request.getSchemeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Scheme", request.getSchemeId()));
        }

        WelfareApplication application = WelfareApplication.builder()
                .citizen(citizen)
                .program(program)
                .scheme(scheme)
                .remarks(request.getRemarks())
                .status(WelfareApplication.ApplicationStatus.SUBMITTED)
                .build();

        application = applicationRepository.save(application);

        auditLogService.log(citizen.getUser().getUserId(), "SUBMIT_APPLICATION", "WELFARE_APPLICATION",
                "Application ID: " + application.getApplicationId() + " submitted for program: " + program.getTitle(), null);

        // Notify citizen
        notificationService.sendNotification(
                citizen.getUser().getUserId(),
                application.getApplicationId(),
                "Your application for " + program.getTitle() + " has been submitted successfully.",
                Notification.NotificationCategory.APPLICATION
        );

        return mapToResponse(application);
    }

    @Override
    public WelfareApplicationResponse getApplicationById(Long id) {
        return mapToResponse(applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application", id)));
    }

    @Override
    public Page<WelfareApplicationResponse> getApplicationsByCitizen(Long citizenId, Pageable pageable) {
        return applicationRepository.findByCitizen_CitizenId(citizenId, pageable).map(this::mapToResponse);
    }

    @Override
    public Page<WelfareApplicationResponse> getApplicationsByProgram(Long programId, Pageable pageable) {
        return applicationRepository.findByProgram_ProgramId(programId, pageable).map(this::mapToResponse);
    }

    @Override
    public Page<WelfareApplicationResponse> getApplicationsByStatus(WelfareApplication.ApplicationStatus status, Pageable pageable) {
        return applicationRepository.findByStatus(status, pageable).map(this::mapToResponse);
    }

    @Override
    public Page<WelfareApplicationResponse> getAllApplications(Pageable pageable) {
        return applicationRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional
    public WelfareApplicationResponse updateApplicationStatus(Long id, WelfareApplication.ApplicationStatus status, String remarks) {
        WelfareApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application", id));
        application.setStatus(status);
        if (remarks != null) application.setRemarks(remarks);
        application = applicationRepository.save(application);

        auditLogService.log(currentUserId(), "UPDATE_APPLICATION_STATUS", "WELFARE_APPLICATION",
                "Application ID: " + id + " status changed to: " + status.name(), null);

        // Notify citizen of status change
        notificationService.sendNotification(
                application.getCitizen().getUser().getUserId(),
                application.getApplicationId(),
                "Your application status has been updated to: " + status.name(),
                Notification.NotificationCategory.APPLICATION
        );

        return mapToResponse(application);
    }

    @Override
    @Transactional
    public void withdrawApplication(Long id, Long citizenId) {
        WelfareApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application", id));

        if (!application.getCitizen().getCitizenId().equals(citizenId)) {
            throw new BusinessException("You are not authorized to withdraw this application");
        }

        if (application.getStatus() == WelfareApplication.ApplicationStatus.DISBURSED
                || application.getStatus() == WelfareApplication.ApplicationStatus.CLOSED) {
            throw new BusinessException("Cannot withdraw application in current status: " + application.getStatus());
        }

        application.setStatus(WelfareApplication.ApplicationStatus.CLOSED);
        applicationRepository.save(application);
        auditLogService.log(currentUserId(), "WITHDRAW_APPLICATION", "WELFARE_APPLICATION",
                "Application ID: " + id + " withdrawn by citizen ID: " + citizenId, null);
    }

    private WelfareApplicationResponse mapToResponse(WelfareApplication a) {
        return WelfareApplicationResponse.builder()
                .applicationId(a.getApplicationId())
                .citizenId(a.getCitizen().getCitizenId())
                .citizenName(a.getCitizen().getName())
                .programId(a.getProgram().getProgramId())
                .programTitle(a.getProgram().getTitle())
                .schemeId(a.getScheme() != null ? a.getScheme().getSchemeId() : null)
                .schemeTitle(a.getScheme() != null ? a.getScheme().getTitle() : null)
                .submittedDate(a.getSubmittedDate())
                .status(a.getStatus())
                .remarks(a.getRemarks())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}
