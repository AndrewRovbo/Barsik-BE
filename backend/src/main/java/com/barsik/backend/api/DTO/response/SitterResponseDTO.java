package com.barsik.backend.api.DTO.response;

import java.math.BigDecimal;

import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
public class SitterResponseDTO {
    Long userId;
    String experienceSummary;
    BigDecimal averageRating;
    Integer reviewsCount;
    Boolean isVerified;
}
