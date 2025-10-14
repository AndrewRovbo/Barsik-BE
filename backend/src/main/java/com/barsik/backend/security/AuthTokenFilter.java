package com.barsik.backend.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
//AuthTokenFilter создаёт Authentication с этими ролями.
@Component
public class AuthTokenFilter extends  OncePerRequestFilter{
    
    @Autowired private JwtUtil jwtUtil;
    @Autowired private CustomUserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if(jwt != null && jwtUtil.validateJwtToken(jwt)){
                String username = jwtUtil.getUserEmailFromToken(jwt);
                Long userId = jwtUtil.getUserIdFromToken(jwt);
                List<String> roles = jwtUtil.getRolesFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                        .toList();

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);//или вместо userDetails.getAuthorities() authorities
        
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        
            SecurityContextHolder.getContext().setAuthentication(authToken);
        
            request.setAttribute("userId", userId);
            }
        } catch (Exception e) {
            //log
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request){
        String headerAuth = request.getHeader("Authorization");
        if(headerAuth != null && headerAuth.startsWith("Bearer ")){
            return headerAuth.substring(7);
        }
        return null;
    }
    
}
