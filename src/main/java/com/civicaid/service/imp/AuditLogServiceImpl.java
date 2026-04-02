package com.civicaid.service.impl;

import com.civicaid.entity.AuditLog;
import com.civicaid.entity.User;
import com.civicaid.exception.ResourceNotFoundException;
import com.civicaid.repository.AuditLogRepository;
import com.civicaid.repository.UserRepository;
import com.civicaid.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Override
    @Async
    @Transactional
    public void log(Long userId, String action, String resource, String details, String ipAddress) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", userId));

            AuditLog auditLog = AuditLog.builder()
                    .user(user)
                    .action(action)
                    .resource(resource)
                    .details(details)
                    .ipAddress(ipAddress)
                    .build();

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to write audit log for user {}: {}", userId, e.getMessage());
        }
    }

    @Override
    public Page<AuditLog> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    @Override
    public Page<AuditLog> getLogsByUser(Long userId, Pageable pageable) {
        return auditLogRepository.findByUser_UserId(userId, pageable);
    }

    @Override
    public Page<AuditLog> getLogsByResource(String resource, Pageable pageable) {
        return auditLogRepository.findByResource(resource, pageable);
    }

    @Override
    public List<AuditLog> getLogsByDateRange(LocalDateTime from, LocalDateTime to) {
        return auditLogRepository.findByTimestampBetween(from, to);
    }
}