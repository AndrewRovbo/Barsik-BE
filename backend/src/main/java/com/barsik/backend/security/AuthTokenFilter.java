package com.barsik.backend.security;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
//AuthTokenFilter создаёт Authentication с этими ролями.
@Component
public class AuthTokenFilter extends OncePerRequestFilter{
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Autowired private JwtUtil jwtUtil;
    //@Autowired private CustomUserDetailsService userDetailsService;

    private String getJwtFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();

    // Публичные эндпоинты пропускаем
        if (path.startsWith("/api/auth/") || path.startsWith("/ws/")) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            Cookie[] cookies = request.getCookies();
            logger.debug("AuthTokenFilter: request path={} cookiesPresent={}", path, cookies != null ? cookies.length : 0);

            String jwt = getJwtFromCookies(request);
            logger.debug("AuthTokenFilter: extracted jwt present={}", jwt != null);

            if(jwt != null && jwtUtil.validateJwtToken(jwt)){
                String email = jwtUtil.getUserEmailFromToken(jwt);
                Long userId = jwtUtil.getUserIdFromToken(jwt);
                List<String> roles = jwtUtil.getRolesFromToken(jwt);
                //UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                        .toList();
                /*CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email); если не доверять токену и делать каждый раз запрос в бд
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); - альтернатива 1 раз в бд потом из кэша доставать reddis*/
                CustomUserDetails userDetails = new CustomUserDetails(email, "", authorities, userId);
               
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            logger.error("AuthTokenFilter error", e);
        }
        filterChain.doFilter(request, response);
    }

    
}
