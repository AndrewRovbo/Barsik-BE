package com.barsik.backend.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.barsik.backend.api.DTO.request.OwnerProfileUpdateRequest;
import com.barsik.backend.api.DTO.request.SitterProfileUpdateRequest;
import com.barsik.backend.api.DTO.request.UserRole;
import com.barsik.backend.api.DTO.request.UserUpdateRequest;
import com.barsik.backend.api.DTO.response.FullProfileResponse;
import com.barsik.backend.entity.User;
import com.barsik.backend.service.ProfileService;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    @Autowired
    private ProfileService profileService;

    /**
     * Получить полный профиль пользователя по ID
     */
    @GetMapping
    public FullProfileResponse getFullProfile(@RequestParam Long userId) {
        return profileService.getFullProfile(userId);
    }

    /**
     * Обновить базовую информацию пользователя
     * PUT /profile/user
     */
    @PutMapping("/user")
    public User updateUser(
            @RequestParam Long userId,
            @RequestBody UserUpdateRequest request
    ) {
        return profileService.updateUserProfile(userId, request);
    }

    /**
     * Обновить профиль владельца
     * PUT /profile/owner
     */
    @PutMapping("/owner")
    public void updateOwnerProfile(
            @RequestParam Long userId,
            @RequestBody OwnerProfileUpdateRequest request
    ) {
        profileService.updateRoleProfile(userId, UserRole.OWNER, request);
    }

    /**
     * Обновить профиль няни
     * PUT /profile/sitter
     */
    @PutMapping("/sitter")
    public void updateSitterProfile(
            @RequestParam Long userId,
            @RequestBody SitterProfileUpdateRequest request
    ) {
        profileService.updateRoleProfile(userId, UserRole.SITTER, request);
    }
}