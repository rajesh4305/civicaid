
package com.civicaid.service.impl;

import com.civicaid.dto.request.AuthRequest;
import com.civicaid.dto.response.AuthResponse;
import com.civicaid.dto.response.UserResponse;
import com.civicaid.entity.User;
import com.civicaid.exception.BusinessException;
import com.civicaid.exception.DuplicateResourceException;
import com.civicaid.exception.ResourceNotFoundException;
import com.civicaid.repository.UserRepository;
import com.civicaid.security.jwt.JwtTokenProvider;
import com.civicaid.service.AuditLogService;
import com.civicaid.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public AuthResponse login(AuthRequest.Login request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String accessToken = jwtTokenProvider.generateAccessToken(request.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(request.getEmail());

        log.info("User logged in: {}", request.getEmail());
        auditLogService.log(user.getUserId(), "LOGIN", "AUTH",
                "User logged in successfully", null);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationMs())
                .user(mapToUserResponse(user))
                .build();
    }

    @Override
    @Transactional
    public AuthResponse register(AuthRequest.Register request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        // Self-registration is restricted to CITIZEN only.
        // All privileged roles (WELFARE_OFFICER, PROGRAM_MANAGER, etc.) must be
        // created by an ADMINISTRATOR via POST /users.
        User.Role role;
        try {
            role = User.Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid role: " + request.getRole());
        }
        if (role != User.Role.CITIZEN) {
            throw new BusinessException(
                    "Self-registration is only allowed for the CITIZEN role. " +
                            "Privileged accounts must be created by an Administrator.");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(role)
                .status(User.UserStatus.ACTIVE)
                .build();

        user = userRepository.save(user);
        log.info("New user registered: {}", request.getEmail());
        auditLogService.log(user.getUserId(), "REGISTER", "AUTH",
                "New account registered with role: " + role.name(), null);

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationMs())
                .user(mapToUserResponse(user))
                .build();
    }

    @Override
    public AuthResponse refreshToken(AuthRequest.RefreshToken request) {
        String token = request.getRefreshToken();
        if (!jwtTokenProvider.validateToken(token)) {
            throw new BusinessException("Invalid or expired refresh token");
        }
        String email = jwtTokenProvider.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String newAccessToken = jwtTokenProvider.generateAccessToken(email);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(email);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationMs())
                .user(mapToUserResponse(user))
                .build();
    }

    @Override
    @Transactional
    public void changePassword(Long userId, AuthRequest.ChangePassword request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed for user: {}", user.getEmail());
        auditLogService.log(userId, "CHANGE_PASSWORD", "AUTH",
                "Password changed successfully", null);
    }

    @Override
    public void logout(String token) {
        // Stateless JWT — client discards token; add token blacklist here if needed
        log.info("User logged out");
    }

    private UserResponse mapToUserResponse(User user) {
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
