package com.project.auth_service.controller;

import com.project.auth_service.dto.request.LoginGoogleRequest;
import com.project.auth_service.service.GoogleOAuth2Service;
import com.project.auth_service.service.LoginOAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class GoogleAuthController {
    private final GoogleOAuth2Service googleOAuth2Service;

    private final LoginOAuth2Service loginOAuth2Service;

    @GetMapping("/google/url")
    public ResponseEntity<String> getGoogleAuthUrl() {
        String url = googleOAuth2Service.generateUrl();
        return ResponseEntity.ok(url);
    }

    @GetMapping("/google/callback")
    public ResponseEntity<?> handleGoogleCallback(
            @RequestParam String code,
            @RequestParam(defaultValue = "google") String loginType) {
        String token = googleOAuth2Service.authenticateAndFetchProfile(loginType, code);
        return ResponseEntity.ok(Map.of("access_token", token));
    }

    @GetMapping("/admin/test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Google Auth Service is running!");
    }

    @PostMapping("/google/login")
    public ResponseEntity<?> loginWithGoogle(@RequestBody LoginGoogleRequest request) {
        return ResponseEntity.ok(loginOAuth2Service.loginOauth2(request));
    }
}
