package com.barsik.backend.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barsik.backend.api.DTO.request.UserRegistrationRequest;
import com.barsik.backend.api.DTO.response.UserResponse;
import com.barsik.backend.entity.User;
import com.barsik.backend.service.RegisterService;
import com.barsik.backend.service.UserService;



@RestController
@RequestMapping("/auth")
public class AuthController {
    /*
     * Sign in — авторизация существующего пользователя; Sign up — регистрация нового пользователя
     */


    @Autowired private RegisterService registerService;

    

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRegistrationRequest request) {
        UserResponse response = registerService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("login")
    public ResponseEntity<String> login() {
        String result = registerService.logInUser();
        return ResponseEntity.ok(result);
    }

}
