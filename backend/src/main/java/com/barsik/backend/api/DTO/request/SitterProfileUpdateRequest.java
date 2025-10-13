package com.barsik.backend.api.DTO.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SitterProfileUpdateRequest {
    private String experienceSummary;
    private BigDecimal averageRating;
    private Integer reviewsCount;
    private Boolean sitterVerified;
}
