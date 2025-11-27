package com.barsik.backend.api.DTO.request;




import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@Schema(description = "authentification request")
public class LogInRequest {
    @Schema(description = "email", example = "jondoe@gmail.com")
    @NotBlank(message = "email is required") @Email private String email;

    @Schema(description = "password", example = "my_1secret1_password")
    @NotBlank @Size(min = 6, max = 32)
    @NotBlank(message = "Password is required") private String password;
}

