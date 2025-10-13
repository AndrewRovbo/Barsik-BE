package com.barsik.backend.api.DTO.response;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.barsik.backend.api.DTO.request.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FullProfileResponse {
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String avatarUrl;
    private String address;
    private UserRole role;
    private String aboutMe;
    private Boolean ownerVerified;

    private String experienceSummary;
    private BigDecimal averageRating;
    private Integer reviewsCount;
    private Boolean sitterVerified;

    private LocalDateTime createdAt;
    private LocalDateTime updateddAt;

    public FullProfileResponse(String email, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.email =email; this.createdAt = createdAt; this.updateddAt = updatedAt;}
}

