package com.barsik.backend.security;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Component
public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, 
                                      WebSocketHandler wsHandler, 
                                      Map<String, Object> attributes) {
        
        CustomUserDetails userDetails = (CustomUserDetails) attributes.get("USER_DETAILS");
        
        if (userDetails != null) {
            return new UsernamePasswordAuthenticationToken(
                    userDetails, 
                    null, 
                    userDetails.getAuthorities()
            );
        }
        return null;
    }
}
