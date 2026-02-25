package com.barsik.backend.api.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.barsik.backend.api.DTO.response.SitterResponseDTO;
import com.barsik.backend.service.SitterService;



@RestController
@RequestMapping("/api/sitters")
public class SitterController {

    @Autowired private SitterService sitterService;

    @GetMapping("/search")
    public ResponseEntity<Page<SitterResponseDTO>> searchSitters(
            @RequestParam(required = false) String serviceType,
            @RequestParam(required = false) String experienceKeyword,
            @RequestParam(required = false) BigDecimal minRating, 
            @RequestParam(required = false) Boolean isVerified,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<SitterResponseDTO> results = sitterService.searchSitters(
                serviceType, experienceKeyword, minRating, isVerified, PageRequest.of(page, size)
        );
        return ResponseEntity.ok(results);
    }

}
