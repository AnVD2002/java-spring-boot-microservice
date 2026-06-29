package com.project.auth_service.controller;

import com.project.auth_service.dto.request.RefreshTokenRequest;
import com.project.auth_service.dto.response.LoginResponse;
import com.project.auth_service.service.auth.RefreshTokenService;
import com.project.common_lib_service.dto.ResponseData;
import com.project.common_lib_service.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Token", description = "Refresh token and logout APIs")
public class TokenController {

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<ResponseData<LoginResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseUtils.success(refreshTokenService.refreshToken(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout by revoking refresh token")
    public ResponseEntity<ResponseData<Void>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        refreshTokenService.logout(request.getRefreshToken());
        return ResponseUtils.success();
    }
}
