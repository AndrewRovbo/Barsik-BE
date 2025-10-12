package com.barsik.backend.api.DTO.response;

import java.time.LocalDateTime;

public record UserResponse(
    String email,
    String firstName,
    String lastName,
    String phoneNumber,
    String avatarUrl,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
