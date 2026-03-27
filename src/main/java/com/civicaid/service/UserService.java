package com.civicaid.service;

import com.civicaid.dto.request.UserRequest;
import com.civicaid.dto.response.UserResponse;
import com.civicaid.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse createUser(UserRequest request);
    Page<UserResponse> getAllUsers(Pageable pageable);
    UserResponse getUserById(Long id);
    UserResponse getUserByEmail(String email);
    UserResponse updateUser(Long id, UserRequest request);
    void updateUserStatus(Long id, User.UserStatus status);
    void deleteUser(Long id);
    Page<UserResponse> getUsersByRole(User.Role role, Pageable pageable);
}
