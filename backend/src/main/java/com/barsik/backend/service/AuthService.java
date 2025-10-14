package com.barsik.backend.service;
/*
import com.barsik.backend.security.JwtService;
import com.barsik.backend.security.JwtUtil;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.barsik.backend.api.DTO.request.UserRegistrationRequest;
import com.barsik.backend.api.DTO.request.UserRole;
import com.barsik.backend.api.DTO.request.LogInRequest;
import com.barsik.backend.api.DTO.request.RegistrationRequestLong;
import com.barsik.backend.api.DTO.response.LogInResponse;
import com.barsik.backend.api.DTO.response.UserResponse;
import com.barsik.backend.entity.Owner;
import com.barsik.backend.entity.Sitter;
import com.barsik.backend.entity.User;
import com.barsik.backend.repository.OwnerRepository;
import com.barsik.backend.repository.SitterRepository;
import com.barsik.backend.repository.UserRepository;



@Service
public class AuthService {
    @Autowired private UserRepository userRepository;
    @Autowired private SitterRepository sitterRepository;
    @Autowired private OwnerRepository ownerRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService; // генерирует JWT
    @Autowired private AuthenticationManager authenticationManager;


    @Transactional
    public void register(RegistrationRequestLong request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        if (request.getRole() == UserRole.SITTER) {
            Sitter sitter = new Sitter();
            sitter.setUser(user);
            sitterRepository.save(sitter);
            user.setSitter(sitter);
        } else if (request.getRole() == UserRole.OWNER) {
            Owner owner = new Owner();
            owner.setUser(user);
            ownerRepository.save(owner);
            user.setOwner(owner);
        }

        userRepository.save(user);
    }

    public LogInResponse login(LogInRequest request) {
        // 1. Authenticate credentials
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(), 
                request.getPassword()
            )
        );

        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);

        //String token = JwtUtil.generateToken(user.getEmail(), user.getId());
//return new AuthResponse(jwtToken);
        // 4. Build and return response
        return new LogInResponse(token);
    }
    
}

*/