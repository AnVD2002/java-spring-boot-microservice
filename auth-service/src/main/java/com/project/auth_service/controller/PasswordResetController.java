package com.project.auth_service.controller;

import com.project.auth_service.dto.request.ForgotPasswordRequest;
import com.project.auth_service.dto.request.ResetPasswordRequest;
import com.project.auth_service.dto.request.VerifyOtpRequest;
import com.project.auth_service.dto.response.VerifyOtpResponse;
import com.project.auth_service.service.auth.PasswordResetService;
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
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseData<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.forgotPassword(request);
        return ResponseUtils.success();
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ResponseData<VerifyOtpResponse>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        VerifyOtpResponse response = passwordResetService.verifyOtp(request);
        return ResponseUtils.success(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseData<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);
        return ResponseUtils.success();
    }
}
