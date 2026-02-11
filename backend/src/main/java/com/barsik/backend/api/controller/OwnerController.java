package com.barsik.backend.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barsik.backend.api.DTO.request.PetRequest;
import com.barsik.backend.security.SecurityUtil;
import com.barsik.backend.service.OwnerService;

@RestController
@RequestMapping("/api/profile")
public class OwnerController {
    
    @Autowired private OwnerService ownerService;
    @Autowired private SecurityUtil securityUtil;
    


    @GetMapping("/owner/pets")
    public ResponseEntity<?> getAllPets(){
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(ownerService.getAllPets(userId));
    };

    @PostMapping("/owner/pets")
    public ResponseEntity<?> addPet(@RequestBody PetRequest petRequest){
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            ownerService.addPet(userId, petRequest);
            return ResponseEntity.status(201).body("Pet added");
        
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/owner/pets/{slug}")
    public ResponseEntity<?> putPet(@PathVariable String slug, @RequestBody PetRequest petRequest) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ownerService.updatePet(userId, slug, petRequest);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/owner/pets/{slug}")
    public ResponseEntity<?> deletePet(@PathVariable String slug){
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ownerService.deletePet(userId, slug);
        return ResponseEntity.ok().build();
    };
}
