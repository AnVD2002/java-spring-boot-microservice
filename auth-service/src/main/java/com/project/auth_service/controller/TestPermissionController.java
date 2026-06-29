package com.project.auth_service.controller;

import com.project.common_lib_service.dto.ResponseData;
import com.project.common_lib_service.jwt.JwtProvider;
import com.project.common_lib_service.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Hidden
@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestPermissionController {

    private final JwtProvider jwtProvider;

    @GetMapping("/public")
    public ResponseEntity<ResponseData<Map<String, String>>> publicEndpoint() {
        return ResponseUtils.success(Map.of(
                "endpoint", "public",
                "message", "Anyone can access this endpoint"
        ));
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<Map<String, String>>> userEndpoint(HttpServletRequest request) {
        return ResponseUtils.success(authorizedResponse("user", "Access granted - USER role verified", request));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Map<String, String>>> adminEndpoint(HttpServletRequest request) {
        return ResponseUtils.success(authorizedResponse("admin", "Access granted - ADMIN role verified", request));
    }

    @GetMapping("/admin-or-user")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ResponseData<Map<String, String>>> adminOrUserEndpoint(HttpServletRequest request) {
        return ResponseUtils.success(authorizedResponse("admin-or-user", "Access granted - ADMIN or USER role verified", request));
    }

    private Map<String, String> authorizedResponse(String endpoint, String message, HttpServletRequest request) {
        String token = extractToken(request);
        String username = token != null ? jwtProvider.extractUserName(token) : "unknown";
        return Map.of(
                "endpoint", endpoint,
                "message", message,
                "username", username
        );
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
