package com.project.common_lib_service.security;

import com.project.common_lib_service.dto.CustomUserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.project.common_lib_service.utils.SecurityHeaders.*;

@RequiredArgsConstructor
public class InternalAuthFilter extends OncePerRequestFilter {

    private final String internalSecret;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String secret = request.getHeader(INTERNAL_SECRET);
        String userId = request.getHeader(USER_ID);
        String username = request.getHeader(USERNAME);
        String rolesHeader = request.getHeader(ROLES);

        if (internalSecret.equals(secret) && username != null && userId != null) {
            List<String> roles = (rolesHeader != null && !rolesHeader.isBlank())
                    ? Arrays.asList(rolesHeader.split(","))
                    : List.of();

            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r.trim()))
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
