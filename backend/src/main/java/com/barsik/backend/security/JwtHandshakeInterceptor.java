package com.barsik.backend.security;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

   
    @Autowired JwtUtil jwtUtil;
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, 
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest servletServerHttpRequest) {
            HttpServletRequest servletRequest = servletServerHttpRequest.getServletRequest();

            // Получить cookie из HTTP запроса
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
            ///для тестов в постман
            if (jwt == null) {
                List<String> authHeaders = request.getHeaders().get("Authorization");
                if (authHeaders != null && !authHeaders.isEmpty()) {
                    jwt = authHeaders.get(0).replace("Bearer ", "");
                }
            }

            // Валидация JWT токена
            if (jwt == null || !jwtUtil.validateJwtToken(jwt)) {
                // Токен отсутствует или не валиден - отклоняем handshake
                return false;
            }

            // Извлечь имя пользователя из токена
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            // Сохранить его в атрибуты сессии WebSocket
            attributes.put("userId", userId);

            return true; // Разрешаем handshake, токен валиден
        }
        return false; // Не ServletServerHttpRequest - отклоняем
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, 
                               WebSocketHandler wsHandler, Exception exception) {}
}
