package com.barsik.backend.service;

import com.barsik.backend.api.DTO.request.UserRegistrationRequest;
import com.barsik.backend.api.DTO.response.UserResponse;
import com.barsik.backend.entity.User;
import com.barsik.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class RegisterService {

    @Autowired
    private UserService userService;

    private User requestToEntity(UserRegistrationRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAvatarUrl(request.getAvatarUrl());
        user.setAddress(request.getAddress());
        // created_at, updated_at — устанавливаются через @PrePersist в Entity
        return user;
    }

    public UserResponse registerUser(UserRegistrationRequest request) {
        User user = requestToEntity(request);
        User savedUser = userService.saveUser(user);

        return new UserResponse(
            savedUser.getEmail(),
            savedUser.getFirstName(),
            savedUser.getLastName(),
            savedUser.getPhoneNumber(),
            savedUser.getAvatarUrl(),
            savedUser.getCreatedAt(),
            savedUser.getUpdatedAt()
        );
    }

    public String logInUser(){
        return "User logged in";
    }
}

