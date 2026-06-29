package com.project.auth_service.controller;

import com.project.auth_service.dto.request.ForgotPasswordRequest;
import com.project.auth_service.dto.request.ResetPasswordRequest;
import com.project.auth_service.dto.request.VerifyOtpRequest;
import com.project.auth_service.dto.response.VerifyOtpResponse;
import com.project.auth_service.service.auth.PasswordResetService;
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
@Tag(name = "Password Reset", description = "Forgot password, OTP verification and password reset APIs")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    @Operation(summary = "Request forgot password OTP")
    public ResponseEntity<ResponseData<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.forgotPassword(request);
        return ResponseUtils.success();
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify password reset OTP")
    public ResponseEntity<ResponseData<VerifyOtpResponse>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        return ResponseUtils.success(passwordResetService.verifyOtp(request));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password")
    public ResponseEntity<ResponseData<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);
        return ResponseUtils.success();
    }
}
