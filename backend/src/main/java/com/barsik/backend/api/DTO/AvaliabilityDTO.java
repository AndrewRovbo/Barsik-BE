package com.barsik.backend.api.DTO;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AvaliabilityDTO {
    private Integer dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
}
