package com.project.auth_service.controller;

import com.project.auth_service.dto.request.RefreshTokenRequest;
import com.project.auth_service.dto.response.LoginResponse;
import com.project.auth_service.service.auth.RefreshTokenService;
import com.project.common_lib_service.dto.ResponseData;
import com.project.common_lib_service.utils.ResponseUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class TokenController {

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/refresh")
    public ResponseEntity<ResponseData<LoginResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = refreshTokenService.refreshToken(request.getRefreshToken());
        return ResponseUtils.success(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseData<Void>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        refreshTokenService.logout(request.getRefreshToken());
        return ResponseUtils.success();
    }
}
