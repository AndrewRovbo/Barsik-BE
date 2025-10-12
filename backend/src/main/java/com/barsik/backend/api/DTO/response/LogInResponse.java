package com.barsik.backend.api.DTO.response;

import com.barsik.backend.api.DTO.request.UserRole;

import lombok.Getter;


@Getter
public class LogInResponse {
    private String token;
    private String tokenType;
    private String email;
    private String firstName;
    private UserRole role;

    public LogInResponse(String token, String tokenType, String email, String firstName, UserRole role) {
        this.token = token;
        this.tokenType = tokenType;
        this.email = email;
        this.firstName = firstName;
        this.role = role;
    }
}