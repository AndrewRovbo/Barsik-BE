package com.barsik.backend.api.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barsik.backend.api.DTO.request.LogInRequest;
import com.barsik.backend.api.DTO.request.RegistrationRequestShort;
import com.barsik.backend.api.DTO.response.FullProfileResponse;
import com.barsik.backend.service.RegisterService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    /*
     * Sign in — авторизация существующего пользователя; Sign up — регистрация нового пользователя
     */

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private RegisterService registerService;

    /*
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LogInRequest logInRequest) {
        //TODO: process POST request
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(logInRequest.getEmail(), logInRequest.getPassword()));
            //final UserDetails userDetails = new UserDetails();
            return ResponseEntity.ok("Login success :" );//+userDetails.getEmail();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
    */

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequestShort request) {
        FullProfileResponse response = registerService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(LogInRequest request) {
        HttpStatus httpStatus;
        try {
            httpStatus = registerService.login(request); 
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("wrong smth in email or password");
        }
        return ResponseEntity.status(httpStatus).build();
    }

}