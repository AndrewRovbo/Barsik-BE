package com.barsik.backend.api.DTO.request;




import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LogInRequest {
    @NotBlank(message = "email is required")
    @Email
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}

