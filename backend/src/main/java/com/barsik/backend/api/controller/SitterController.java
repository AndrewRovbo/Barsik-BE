package com.barsik.backend.api.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.barsik.backend.entity.Sitter;
import com.barsik.backend.service.SitterService;



@RestController
@RequestMapping("/api/sitters")
public class SitterController {

    @Autowired private SitterService sitterService;
    
    /*
    @GetMapping("/search")
    public ResponseEntity<Page<ChatMessageDTO>> seacrhSittersWirhNoFilters(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size){
        return ResponseEntity.ok().build();
    };
*/
    @GetMapping("/search")
    public List<Sitter> searchSitters(
            @RequestParam(required = false) String experienceKeyword,
            @RequestParam(required = false) BigDecimal minRating, @RequestParam(required = false) Boolean isVerified
            ) {
        return sitterService.searchSitters(experienceKeyword, minRating, isVerified);
    }
}
