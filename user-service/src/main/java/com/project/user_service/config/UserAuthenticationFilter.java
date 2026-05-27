package com.project.user_service.config;

import com.project.common_lib_service.dto.CustomUserPrincipal;
import com.project.common_lib_service.utils.SecurityHeaders;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class UserAuthenticationFilter extends OncePerRequestFilter {

    @Value("${internal.secret}")
    private String internalSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String secret = request.getHeader(SecurityHeaders.INTERNAL_SECRET);
        String userId = request.getHeader(SecurityHeaders.USER_ID);
        String username = request.getHeader(SecurityHeaders.USERNAME);
        String rolesHeader = request.getHeader(SecurityHeaders.ROLES);

        // Reject requests not originating from the gateway
        if (!internalSecret.equals(secret)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (userId != null && rolesHeader != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            List<String> roles = Arrays.stream(rolesHeader.split(","))
                    .map(String::trim)
                    .toList();

            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                    .toList();

            CustomUserPrincipal principal = CustomUserPrincipal.builder()
                    .userId(UUID.fromString(userId))
                    .username(username)
                    .roles(roles)
                    .build();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
