package com.civicaid.service;

import com.civicaid.dto.request.AuthRequest;
import com.civicaid.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(AuthRequest.Login request);
    AuthResponse register(AuthRequest.Register request);
    AuthResponse refreshToken(AuthRequest.RefreshToken request);
    void changePassword(Long userId, AuthRequest.ChangePassword request);
    void logout(String token);
}
