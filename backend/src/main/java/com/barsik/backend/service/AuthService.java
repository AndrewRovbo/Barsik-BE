/*package com.barsik.backend.service;

import com.barsik.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.barsik.backend.api.DTO.request.UserRegistrationRequest;
import com.barsik.backend.api.DTO.request.UserRole;
import com.barsik.backend.api.DTO.request.LogInRequest;
import com.barsik.backend.api.DTO.response.LogInResponse;
import com.barsik.backend.api.DTO.response.UserResponse;
import com.barsik.backend.entity.Owner;
import com.barsik.backend.entity.Sitter;
import com.barsik.backend.entity.User;



@Service
public class AuthService {

    @Autowired
    private UserService userService;
    
    @Autowired
    private OwnerService ownerService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private SitterService sitterService;
    @Autowired
    private AuthenticationManager authenticationManager;

    private User requestToEntity(UserRegistrationRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAvatarUrl(request.getAvatarUrl());
        user.setAddress(request.getAddress());
       
        return user;
    }

    public UserResponse registerUser(UserRegistrationRequest request) {
        User user = requestToEntity(request);
        User savedUser = userService.saveUser(user);

        UserRole saveRole = request.getUserRole();
        if (saveRole == UserRole.OWNER) {
            Owner owner = new Owner(user);
            ownerService.createOwnerProfile(owner);
            //ownerRepository.save(owner);
        } else if (saveRole == UserRole.SITTER) {
            Sitter sitter = new Sitter(user);
            sitterService.createSitterProfile(sitter);
            //sitterRepository.save(sitter);
        }

        return new UserResponse(
            savedUser.getEmail(),
            savedUser.getFirstName(),
            savedUser.getLastName(),
            savedUser.getPhoneNumber(),
            savedUser.getAvatarUrl(),
            saveRole,
            savedUser.getCreatedAt(),
            savedUser.getUpdatedAt()
        );
    }

    public LogInResponse logInUser(LogInRequest request) {
        // 1. Authenticate credentials
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(), 
                request.getPassword()
            )
        );

        User user = userService.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));


        String token = JwtUtil.generateToken(user.getEmail(), user.getId());

        // 4. Build and return response
        return new LogInResponse(
            token,
            "Bearer",
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            profileService.getUserRole(user.getId())
        );
    }
    
}

*/