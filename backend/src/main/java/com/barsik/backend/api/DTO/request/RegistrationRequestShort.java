package com.barsik.backend.api.DTO.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Schema(description = "registration request")
public class RegistrationRequestShort {
    @Schema(description = "email", example = "jondoe@gmail.com")
    @Email
    private String email;

    @Schema(description = "password", example = "my_1secret1_password")
    @NotBlank @Size(min = 6, max = 32)
    private String password;

    /*public RegistrationRequestShort(String phoneNumber, String password) {
        this.phoneNumber = phoneNumber;
        this.password = password;
    }*/    
}
