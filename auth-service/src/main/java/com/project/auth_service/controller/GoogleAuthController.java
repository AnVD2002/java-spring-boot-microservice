package com.project.auth_service.controller;

import com.project.auth_service.service.GoogleOAuth2Service;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class GoogleAuthController {
    private final GoogleOAuth2Service googleOAuth2Service;

    @GetMapping("/google/url")
    public ResponseEntity<String> getGoogleAuthUrl(HttpServletRequest request) {
        String url = googleOAuth2Service.generateUrl(request, "google");
        return ResponseEntity.ok(url);
    }

    @GetMapping("/google/callback")
    public ResponseEntity<?> handleGoogleCallback(
            @RequestParam String code,
            @RequestParam(defaultValue = "google") String loginType) {
        String token = googleOAuth2Service.authenticateAndFetchProfile(loginType, code);
        return ResponseEntity.ok(Map.of("access_token", token));
    }
}
