package com.civicaid.dto.request;

import com.civicaid.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank
    private String name;

    @NotBlank @Email
    private String email;

    /** Required only when creating a new user (POST). Ignored on updates (PUT). */
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private String phone;

    @NotNull
    private User.Role role;

    private User.UserStatus status;
}