package com.barsik.backend.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barsik.backend.api.DTO.request.OwnerProfileUpdateRequest;
import com.barsik.backend.api.DTO.request.SitterProfileUpdateRequest;
import com.barsik.backend.api.DTO.request.UserRole;
import com.barsik.backend.api.DTO.request.UserUpdateRequest;
import com.barsik.backend.api.DTO.response.FullProfileResponse;
import com.barsik.backend.entity.User;
import com.barsik.backend.security.SecurityUtil;
import com.barsik.backend.service.ProfileService;


@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    @Autowired private ProfileService profileService;
    @Autowired private SecurityUtil securityUtil;


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
}