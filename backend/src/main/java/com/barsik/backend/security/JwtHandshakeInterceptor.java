package com.barsik.backend.security;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

   
    @Autowired JwtUtil jwtUtil;
    @Autowired 
    private CustomUserDetailsService customUserDetailsService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, 
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest servletServerHttpRequest) {
            HttpServletRequest servletRequest = servletServerHttpRequest.getServletRequest();

            Cookie[] cookies = servletRequest.getCookies();
            String jwt = null;
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("JWT_TOKEN".equals(cookie.getName())) {
                        jwt = cookie.getValue();
                        break;
                    }
                }
            }

            if (jwt == null || !jwtUtil.validateJwtToken(jwt)) {
                return false;
            }

            //Long userId = jwtUtil.getUserIdFromToken(jwt);
            String username = jwtUtil.getUserEmailFromToken(jwt);
            try{
                CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(username); 
                attributes.put("USER_DETAILS", userDetails); 
            }
            catch(UsernameNotFoundException ex) {
                return false;
            }
            
            return true;
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, 
                               WebSocketHandler wsHandler, Exception exception) {}
}
