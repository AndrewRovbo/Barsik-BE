package com.barsik.backend.api.controller;


import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barsik.backend.api.DTO.request.LogInRequest;
import com.barsik.backend.api.DTO.request.RegistrationRequestLong;
import com.barsik.backend.api.DTO.response.LogInResponse;
import com.barsik.backend.entity.Owner;
import com.barsik.backend.entity.Sitter;
import com.barsik.backend.entity.User;
import com.barsik.backend.repository.OwnerRepository;
import com.barsik.backend.repository.SitterRepository;
import com.barsik.backend.repository.UserRepository;
import com.barsik.backend.security.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private OwnerRepository ownerRepository;
    @Autowired private SitterRepository sitterRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder encoder;
    @Autowired private JwtUtil jwtUtil;



    

    /*
     * Sign in — авторизация существующего пользователя; Sign up — регистрация нового пользователя
    */
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LogInRequest logInRequest, HttpServletResponse response) {


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
            .secure(true)// только по HTTPS
            .path("/")// доступно на всех путях
            .maxAge(86400)
            .sameSite("Strict")//защита от CSRF
            .build();

    response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

    // 6. Возвращаем ответ без токена в теле
    return ResponseEntity.ok(new LogInResponse(userDetails.getUsername(), roles));
    }
    
/*

*/
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequestLong request) {
        if(userRepository.existsByEmail(request.getEmail())){ return ResponseEntity.badRequest().body("User with this email exist");}
        User user = new User(request.getFirstName(), request.getLastName(), request.getEmail(), encoder.encode(request.getPassword()), request.getPhoneNumber());
        userRepository.save(user);

        if(null == request.getRole()) {
            return ResponseEntity.badRequest().body("User role must be specified (OWNER or SITTER)");
        } else switch (request.getRole()) {
            case OWNER -> {
                Owner owner = new Owner();
                owner.setUser(user);
                ownerRepository.save(owner);
            }
            case SITTER -> {
                Sitter sitter = new Sitter();
                sitter.setUser(user);
                sitter.setAverageRating(BigDecimal.ZERO);
                sitter.setReviewsCount(0);
                sitterRepository.save(sitter);
            }
        }
        return  ResponseEntity.status(201).body("User registered successfully");
    }

}