package com.barsik.backend.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barsik.backend.api.DTO.request.LogInRequest;
import com.barsik.backend.api.DTO.request.RegistrationRequestLong;
import com.barsik.backend.api.DTO.response.LogInResponse;
import com.barsik.backend.entity.User;
import com.barsik.backend.repository.UserRepository;
import com.barsik.backend.security.JwtUtil;
import com.barsik.backend.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private JwtUtil jwtUtil;
    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;
    @Value("${app.cookie.sameSite:Lax}")
    private String cookieSameSite;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LogInRequest logInRequest, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(logInRequest.getEmail(), logInRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(r -> r.replace("ROLE_", ""))
                    .toList();

            String jwt = jwtUtil.generateToken(userDetails.getUsername(), user.getId(), roles);
            ResponseCookie jwtCookie = ResponseCookie.from("JWT_TOKEN", jwt)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(86400)
                .sameSite(cookieSameSite)
                .build();

            response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
            

            return ResponseEntity.ok(new LogInResponse(user.getId(),userDetails.getUsername(), roles));
        } catch (BadCredentialsException e) {
            System.out.println("BadCredentialsException: " + e.getMessage());
            return ResponseEntity.status(401).body("Invalid email or password");
        } catch (UsernameNotFoundException e) {
            System.out.println("UsernameNotFoundException: " + e.getMessage());
            return ResponseEntity.status(401).body("User not found");
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(401).body("Authentication failed: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequestLong request) {
        try {
            userService.registerUser(request);
            return ResponseEntity.status(201).body("User registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Registration failed: " + e.getMessage());
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("JWT_TOKEN", "")
            .path("/")
            .httpOnly(true)
            .secure(cookieSecure)
            .sameSite(cookieSameSite)
            .maxAge(0)
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        
        return ResponseEntity.noContent().build();
    }
}


