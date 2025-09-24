package com.project.api_gateway_service.config;

import com.project.api_gateway_service.Security.MutableHttpServletRequest;
import com.project.api_gateway_service.jwt.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        // validate token
        if (jwtProvider.isTokenExpired(jwt)) {
            handleErrorResponse(response, "Token expired");
            return;
        }

        if (!jwtProvider.isSignatureValid(jwt)) {
            handleErrorResponse(response, "Invalid token signature");
            return;
        }

        // extract claims
        String username = jwtProvider.extractUserName(jwt);
        Long accountId = jwtProvider.extractAccountId(jwt);
        String role = jwtProvider.extractUserRole(jwt);

        // add info into request header (so service can use it)
        MutableHttpServletRequest wrappedRequest = new MutableHttpServletRequest(request);
        if (username != null) wrappedRequest.putHeader("X-Username", username);
        if (accountId != null) wrappedRequest.putHeader("X-UserId", accountId.toString());
        if (role != null) wrappedRequest.putHeader("X-Role", role);

        filterChain.doFilter(wrappedRequest, response);
    }

    private void handleErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"code\": \"" + HttpStatus.UNAUTHORIZED.value() + "\", \"message\": \"" + message + "\"}");
        response.getWriter().flush();
    }
}
