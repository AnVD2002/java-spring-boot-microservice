package com.project.auth_service.controller;

import com.project.auth_service.dto.request.AccountRegistrationRequest;
import com.project.auth_service.dto.request.LoginGoogleRequest;
import com.project.auth_service.dto.response.GoogleCallbackResponse;
import com.project.auth_service.dto.response.LoginResponse;
import com.project.auth_service.service.auth.LoginOAuth2Service;
import com.project.auth_service.service.facade.RegisterAccountFacade;
import com.project.auth_service.service.provider.GoogleOAuth2Service;
import com.project.common_lib_service.dto.ResponseData;
import com.project.common_lib_service.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/google")
@RequiredArgsConstructor
@Tag(name = "Google Auth", description = "Google OAuth2 login and registration APIs")
public class GoogleAuthController {

    private static final String DEVICE_ID_HEADER = "X-Device-ID";

    private final GoogleOAuth2Service googleOAuth2Service;
    private final LoginOAuth2Service loginOAuth2Service;
    private final RegisterAccountFacade registerAccountFacade;

    @GetMapping("/url")
    @Operation(summary = "Get Google OAuth URL")
    public ResponseEntity<ResponseData<String>> getGoogleAuthUrl() {
        return ResponseUtils.success(googleOAuth2Service.generateUrl());
    }

    @GetMapping("/callback")
    @Operation(summary = "Handle Google OAuth callback")
    public ResponseEntity<ResponseData<GoogleCallbackResponse>> handleGoogleCallback(
            @RequestParam String code,
            @RequestParam(defaultValue = "google") String loginType) {
        String token = googleOAuth2Service.authenticateAndFetchProfile(loginType, code);
        return ResponseUtils.success(GoogleCallbackResponse.builder().idToken(token).build());
    }

    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Test Google auth service", description = "Development endpoint protected by ADMIN role")
    public ResponseEntity<ResponseData<String>> testEndpoint() {
        return ResponseUtils.success("Google Auth Service is running!");
    }

    @PostMapping("/login")
    @Operation(summary = "Login with Google")
    public ResponseEntity<ResponseData<LoginResponse>> loginWithGoogle(
            @Valid @RequestBody LoginGoogleRequest request,
            HttpServletRequest httpServletRequest) {
        return ResponseUtils.success(loginOAuth2Service.loginOauth2(request, httpServletRequest.getHeader(DEVICE_ID_HEADER)));
    }

    @PostMapping("/register")
    @Operation(summary = "Register with Google")
    public ResponseEntity<ResponseData<Void>> registerWithGoogle(@Valid @RequestBody AccountRegistrationRequest request) {
        registerAccountFacade.register(request);
        return ResponseUtils.success();
    }
}
