package com.project.auth_service.controller;

import com.project.common_lib_service.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestPermissionController {

    private final JwtProvider jwtProvider;

    @GetMapping("/public")
    public ResponseEntity<Map<String, String>> publicEndpoint() {
        return ResponseEntity.ok(Map.of(
                "endpoint", "public",
                "message", "Anyone can access this endpoint"
        ));
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> userEndpoint(HttpServletRequest request) {
        String token = extractToken(request);
        String username = token != null ? jwtProvider.extractUserName(token) : "unknown";
        return ResponseEntity.ok(Map.of(
                "endpoint", "user",
                "message", "Access granted — USER role verified",
                "username", username
        ));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> adminEndpoint(HttpServletRequest request) {
        String token = extractToken(request);
        String username = token != null ? jwtProvider.extractUserName(token) : "unknown";
        return ResponseEntity.ok(Map.of(
                "endpoint", "admin",
                "message", "Access granted — ADMIN role verified",
                "username", username
        ));
    }

    @GetMapping("/admin-or-user")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Map<String, String>> adminOrUserEndpoint(HttpServletRequest request) {
        String token = extractToken(request);
        String username = token != null ? jwtProvider.extractUserName(token) : "unknown";
        return ResponseEntity.ok(Map.of(
                "endpoint", "admin-or-user",
                "message", "Access granted — ADMIN or USER role verified",
                "username", username
        ));
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
