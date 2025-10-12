package com.barsik.backend.api.DTO.request;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SitterProfileUpdateRequest {
    private String experienceSummary;
    private BigDecimal averageRating;
    private Integer reviewsCount;
    private Boolean sitterVerified;
}
