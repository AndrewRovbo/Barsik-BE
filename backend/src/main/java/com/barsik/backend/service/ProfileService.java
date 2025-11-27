package com.barsik.backend.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barsik.backend.api.DTO.request.OwnerProfileUpdateRequest;
import com.barsik.backend.api.DTO.request.SitterProfileUpdateRequest;
import com.barsik.backend.api.DTO.request.UserRole;
import com.barsik.backend.api.DTO.request.UserUpdateRequest;
import com.barsik.backend.api.DTO.response.FullProfileResponse;
import com.barsik.backend.entity.Owner;
import com.barsik.backend.entity.Sitter;
import com.barsik.backend.entity.User;
import com.barsik.backend.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;



@Service
public class ProfileService {
    


    @Autowired private OwnerService ownerService;
    @Autowired private SitterService sitterService;
    @Autowired private UserRepository userRepository;
    
    @Autowired private UserService userService;

//delete
    @Transactional
    public void deleteUser(Long userId){
        userRepository.deleteById(userId);
    }
    @Transactional
    public void deleteOwner(Long userId){/*
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Owner owner = user.getOwner();
        if (owner != null) {
            user.setOwner(null);
            userRepository.save(user);
            //sitterRepository.deleteById(sitter.getUserId());
        }*/
        ownerService.deleteOwner(userId);
        updateUpdatedAt(userId);
        
    }
    @Transactional
    public void deleteSitter(Long userId){
        sitterService.deleteSitter(userId);
        updateUpdatedAt(userId);
        /*User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Sitter sitter = user.getSitter();
        if (sitter != null) {
            user.setSitter(null);
            userRepository.save(user);
            //sitterRepository.deleteById(sitter.getUserId());
        }*/
    }



//update
    @Transactional
    public User updateUserProfile(Long userId, UserUpdateRequest request) {
        return userService.updateUser(userId, request);
    }

    @Transactional
    public void updateRoleProfile(Long userId, UserRole role, Object request) {
        switch (role) {
            case OWNER -> {
                if (!(request instanceof OwnerProfileUpdateRequest ownerRequest)) {
                throw new IllegalArgumentException("Invalid request type for role OWNER");
                }
                ownerService.updateOwnerProfile(userId, ownerRequest);
                updateUpdatedAt(userId);
            }
            case SITTER -> {
                if (!(request instanceof SitterProfileUpdateRequest sitterRequest)) {
                throw new IllegalArgumentException("Invalid request type for role SITTER");
                }
                sitterService.updateSitterProfile(userId, sitterRequest);
                updateUpdatedAt(userId);                
            }
            default -> throw new IllegalArgumentException("Unknown role: " + role);
        }
       
    }
    
    private void updateUpdatedAt(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    

    public Set<UserRole> getUserRoles(Long userId) {
        Set<UserRole> roles = new HashSet<>();
        if (ownerService.existsByUserId(userId)) {
            roles.add(UserRole.OWNER);
        }
        if (sitterService.existsByUserId(userId)) {
            roles.add(UserRole.SITTER);
        }
        return roles;
    }


    public boolean canUpdateRole(Long userId, UserRole role) {
        return getUserRoles(userId).contains(role);
    }

    @Transactional
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


        Set<UserRole> roles = getUserRoles(userId);
        resp.setRoles(roles);

        if (roles.contains(UserRole.OWNER)) {
            Owner owner = ownerService.getByUserId(userId);
            resp.setAboutMe(owner.getAboutMe());
            resp.setOwnerVerified(owner.getIsVerified());
        }
        if (roles.contains(UserRole.SITTER)) {
            Sitter sitter = sitterService.getByUserId(userId);
            resp.setExperienceSummary(sitter.getExperienceSummary());
            resp.setAverageRating(sitter.getAverageRating());
            resp.setReviewsCount(sitter.getReviewsCount());
            resp.setSitterVerified(sitter.getIsVerified());
        }
        

        return resp;
    }
    
}
