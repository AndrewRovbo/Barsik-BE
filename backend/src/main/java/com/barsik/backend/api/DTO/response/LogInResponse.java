package com.barsik.backend.api.DTO.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class LogInResponse {
    private long id;
    private String username;
    private List<String> roles;
}
