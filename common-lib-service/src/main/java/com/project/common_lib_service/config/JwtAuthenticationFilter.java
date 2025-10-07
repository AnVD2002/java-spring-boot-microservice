package com.project.common_lib_service.config;

import com.project.common_lib_service.dto.CustomUserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String accountIdHeader = request.getHeader("X-AccountId");
        String username = request.getHeader("X-Username");
        String roleHeader = request.getHeader("X-Role");

        List<String> roles = new ArrayList<>();
        if (roleHeader != null && !roleHeader.isBlank()) {
            roles = Arrays.stream(roleHeader.split(","))
                    .map(String::trim)
                    .filter(r -> !r.isEmpty())
                    .toList();
        }

        CustomUserPrincipal principal = CustomUserPrincipal.builder()
                .userId(UUID.fromString(accountIdHeader))
                .username(username)
                .roles(roles)
                .build();

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }


}
