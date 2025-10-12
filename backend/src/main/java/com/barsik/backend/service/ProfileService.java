package com.barsik.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barsik.backend.api.DTO.request.FullProfileResponse;
import com.barsik.backend.api.DTO.request.OwnerProfileUpdateRequest;
import com.barsik.backend.api.DTO.request.SitterProfileUpdateRequest;
import com.barsik.backend.api.DTO.request.UserRole;
import com.barsik.backend.api.DTO.request.UserUpdateRequest;
import com.barsik.backend.entity.Owner;
import com.barsik.backend.entity.Sitter;
import com.barsik.backend.entity.User;



@Service
@Transactional
public class ProfileService {
    

    @Autowired private UserService userService;
    @Autowired private OwnerService ownerService;
    @Autowired private SitterService sitterService;


        /**
     * Обновление базового профиля пользователя
     */
    public User updateUserProfile(Long userId, UserUpdateRequest request) {
       return userService.updateUser(userId, request);
    }


    public void updateRoleProfile(Long userId, UserRole role, Object updateRequest) {
        switch (role) {
            case OWNER -> ownerService.updateOwnerProfile(userId, (OwnerProfileUpdateRequest) updateRequest);
            case SITTER -> sitterService.updateSitterProfile(userId, (SitterProfileUpdateRequest) updateRequest);
            default -> throw new IllegalArgumentException("Unknown role: " + role);
        }
    }

    /**
     * Получить роль пользователя
    */
    public UserRole getUserRole(Long userId) {
        if (ownerService.isUserOwner(userId)) {
            return UserRole.OWNER;
        } else if (sitterService.isUserSitter(userId)) {
            return UserRole.SITTER;
        }
        throw new RuntimeException("User has no role assigned");
    }


    public boolean canUpdateRole(Long userId, UserRole role) {
        return getUserRole(userId) == role;
    }


    public FullProfileResponse getFullProfile(Long userId) {
   
        User user = userService.findById(userId);

        FullProfileResponse resp = new FullProfileResponse();
        resp.setEmail(user.getEmail());
        resp.setFirstName(user.getFirstName());
        resp.setLastName(user.getLastName());
        resp.setPhoneNumber(user.getPhoneNumber());
        resp.setAvatarUrl(user.getAvatarUrl());
        resp.setAddress(user.getAddress());
        resp.setCreatedAt(user.getCreatedAt());
        resp.setUpdateddAt(user.getCreatedAt());


        UserRole role = this.getUserRole(userId);
        resp.setRole(role);

        switch (role) {
            case OWNER -> {
                Owner owner = ownerService.getByUserId(userId);
                resp.setAboutMe(owner.getAboutMe());
                resp.setOwnerVerified(owner.getIsVerified());
            }
            case SITTER -> {
                Sitter sitter = sitterService.getByUserId(userId);
                resp.setExperienceSummary(sitter.getExperienceSummary());
                resp.setAverageRating(sitter.getAverageRating());
                resp.setReviewsCount(sitter.getReviewsCount());
                resp.setSitterVerified(sitter.getIsVerified());
            }
        }

        return resp;
    }


    private FullProfileResponse mapUserToResponse(User user) {
        FullProfileResponse response = new FullProfileResponse();
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setAddress(user.getAddress());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
    
}
