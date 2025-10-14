package com.barsik.backend.api.DTO.response;

import java.util.List;

import com.barsik.backend.api.DTO.request.UserRole;
import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class LogInResponse {
    @JsonAlias("token")
    private String token;
    private String Username;
    private List<String> roles;
}