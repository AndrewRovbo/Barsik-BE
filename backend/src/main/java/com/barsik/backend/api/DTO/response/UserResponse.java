package com.barsik.backend.api.DTO.response;

import java.time.LocalDateTime;

import com.barsik.backend.api.DTO.request.UserRole;

public record UserResponse(
    String email,
    String firstName,
    String lastName,
    String phoneNumber,
    String avatarUrl,
    UserRole role,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
