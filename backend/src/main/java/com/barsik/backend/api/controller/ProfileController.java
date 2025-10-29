package com.barsik.backend.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barsik.backend.api.DTO.request.OwnerProfileUpdateRequest;
import com.barsik.backend.api.DTO.request.PetRequest;
import com.barsik.backend.api.DTO.request.SitterProfileUpdateRequest;
import com.barsik.backend.api.DTO.request.UserRole;
import com.barsik.backend.api.DTO.request.UserUpdateRequest;
import com.barsik.backend.api.DTO.response.FullProfileResponse;
import com.barsik.backend.entity.User;
import com.barsik.backend.security.SecurityUtil;
import com.barsik.backend.service.OwnerService;
import com.barsik.backend.service.ProfileService;

import jakarta.servlet.http.HttpServletResponse;



//не удалает ситтеров и овнеров
@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    @Autowired private ProfileService profileService;
    @Autowired private SecurityUtil securityUtil;
    @Autowired private OwnerService ownerService;


    /**
     * Получить полный профиль пользователя по ID
     */
    @GetMapping
    public ResponseEntity<FullProfileResponse> getFullProfile() {
        Long userId = securityUtil.getCurrentUserId();
        FullProfileResponse profile = profileService.getFullProfile(userId);
        return ResponseEntity.ok(profile);
    }

    /**
     * Обновить базовую информацию пользователя
     * PUT /profile/user
     */
    @PutMapping("/user")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateRequest updateRequest
    ) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User updatedUser = profileService.updateUserProfile(userId, updateRequest);
        return ResponseEntity.ok(updatedUser);
    }
    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUser(HttpServletResponse response){
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        profileService.deleteUser(userId);
        response.addHeader(HttpHeaders.SET_COOKIE, delCookie().toString());
        return ResponseEntity.noContent().build();

    }



    /**
     * Обновить профиль владельца
     * PUT /profile/owner
     */
    @PutMapping("/owner")
    public ResponseEntity<?> updateOwnerProfile(@RequestBody OwnerProfileUpdateRequest updateRequest
    ) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        profileService.updateRoleProfile(userId, UserRole.OWNER, updateRequest);
        return ResponseEntity.ok(updateRequest);
        
    }
    @DeleteMapping("/owner")
    public ResponseEntity<?> deleteOwner(HttpServletResponse response){
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        profileService.deleteOwner(userId);
        response.addHeader(HttpHeaders.SET_COOKIE, delCookie().toString());
        return ResponseEntity.noContent().build();

    }

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

    


    @PutMapping("/sitter")
    public ResponseEntity<?> updateSitterProfile(@RequestBody SitterProfileUpdateRequest updateRequest
    ) {
        Long userId = securityUtil.getCurrentUserId();
        if(userId == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        profileService.updateRoleProfile(userId, UserRole.SITTER, updateRequest);
        return ResponseEntity.ok(updateRequest);
    }

    @DeleteMapping("/sitter")
    public ResponseEntity<?> deleteSitter(HttpServletResponse response){
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        profileService.deleteSitter(userId);
        response.addHeader(HttpHeaders.SET_COOKIE, delCookie().toString());
        return ResponseEntity.noContent().build();

    }

    private ResponseCookie delCookie(){
        ResponseCookie cookie = ResponseCookie.from("JWT_TOKEN", "")
            .path("/")
            .httpOnly(true)
            .secure(true)
            .sameSite("Strict")
            .maxAge(0)
            .build();
        return cookie;
    }


}