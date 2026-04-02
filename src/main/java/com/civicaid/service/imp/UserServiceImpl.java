package com.civicaid.service.impl;

import com.civicaid.dto.request.UserRequest;
import com.civicaid.dto.response.UserResponse;
import com.civicaid.entity.User;
import com.civicaid.exception.BusinessException;
import com.civicaid.exception.DuplicateResourceException;
import com.civicaid.exception.ResourceNotFoundException;
import com.civicaid.repository.UserRepository;
import com.civicaid.service.AuditLogService;
import com.civicaid.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    /** Resolves the currently authenticated user's ID from the SecurityContext. */
    private Long currentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).map(User::getUserId).orElse(null);
    }

    @Override
    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BusinessException("Password is required when creating a new user.");
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole())
                .status(request.getStatus() != null ? request.getStatus() : User.UserStatus.ACTIVE)
                .build();
        User saved = userRepository.save(user);
        auditLogService.log(currentUserId(), "CREATE_USER", "USER",
                "Created user: " + saved.getEmail() + " with role: " + saved.getRole().name(), null);
        return mapToResponse(saved);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public UserResponse getUserById(Long id) {
        return mapToResponse(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id)));
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        return mapToResponse(userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email)));
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        if (request.getRole() != null) user.setRole(request.getRole());
        if (request.getStatus() != null) user.setStatus(request.getStatus());
        User saved = userRepository.save(user);
        auditLogService.log(currentUserId(), "UPDATE_USER", "USER",
                "Updated user ID: " + id, null);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long id, User.UserStatus status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        user.setStatus(status);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) throw new ResourceNotFoundException("User", id);
        auditLogService.log(currentUserId(), "DELETE_USER", "USER",
                "Deleted user ID: " + id, null);
        userRepository.deleteById(id);
    }

    @Override
    public Page<UserResponse> getUsersByRole(User.Role role, Pageable pageable) {
        return userRepository.findByRole(role, pageable).map(this::mapToResponse);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}