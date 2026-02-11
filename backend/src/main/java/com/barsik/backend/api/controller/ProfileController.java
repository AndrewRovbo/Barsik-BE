package com.barsik.backend.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barsik.backend.api.DTO.AvaliabilityDTO;
import com.barsik.backend.api.DTO.request.OwnerProfileUpdateRequest;
import com.barsik.backend.api.DTO.request.SitterProfileUpdateRequest;
import com.barsik.backend.api.DTO.request.UserRole;
import com.barsik.backend.api.DTO.request.UserUpdateRequest;
import com.barsik.backend.api.DTO.response.FullProfileResponse;
import com.barsik.backend.entity.User;
import com.barsik.backend.security.SecurityUtil;
import com.barsik.backend.service.OwnerService;
import com.barsik.backend.service.ProfileService;
import com.barsik.backend.service.SitterAvailabilityService;

import jakarta.servlet.http.HttpServletResponse;




@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    @Autowired private ProfileService profileService;
    @Autowired private SecurityUtil securityUtil;
    @Autowired private OwnerService ownerService;
    @Autowired private SitterAvailabilityService sitterAvailabilityService;

    @GetMapping
    public ResponseEntity<FullProfileResponse> getFullProfile() {
        Long userId = securityUtil.getCurrentUserId();
        FullProfileResponse profile = profileService.getFullProfile(userId);
        return ResponseEntity.ok(profile);
    }

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
    
    @PutMapping("/sitter/avaliability")
    public ResponseEntity<?> updateAvailability(@RequestBody List<AvaliabilityDTO> request){
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        sitterAvailabilityService.updateAvailability(userId, request);
        //обработка исключений
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/sitter/avaliability")
    public ResponseEntity<?> getMethodName() {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<AvaliabilityDTO> lst =  sitterAvailabilityService.findBySitterId(userId);
        return ResponseEntity.ok(lst);
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
