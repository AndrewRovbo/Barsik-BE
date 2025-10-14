package com.barsik.backend.security;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
//JwtUtil добавляет и читает роли в токене.
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-time}")
    private Long jwtExpiration;

    private SecretKey key;

    @PostConstruct
    public void init(){
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email, Long userId, List<String> roles){
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("roles", roles);
        
        return Jwts.builder()
                    .subject(email)
                    .claims(claims)
                    .issuedAt(new Date())
                    .expiration(new Date((new Date()).getTime()+jwtExpiration))
                    .signWith(key)
                    .compact();
    }
    public String getUserEmailFromToken(String token){
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
    public Map<String, Object> getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserIdFromToken(String token) {
        return ((Number) getAllClaims(token).get("userId")).longValue();
    }

    
    public List<String> getRolesFromToken(String token) {
        Object roles = getAllClaims(token).get("roles");
        if(roles instanceof List<?> list) {
            return list.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();
        }
        return Collections.emptyList();
    }
    public boolean validateJwtToken(String token){
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
            
        } catch (Exception e) {
            //to log

        } return false;
    }
}
