package com.project.auth_service.service.auth;

import com.project.auth_service.dto.request.ForgotPasswordRequest;
import com.project.auth_service.dto.request.ResetPasswordRequest;
import com.project.auth_service.dto.request.VerifyOtpRequest;
import com.project.auth_service.dto.response.VerifyOtpResponse;

public interface PasswordResetService {
    void forgotPassword(ForgotPasswordRequest request);
    VerifyOtpResponse verifyOtp(VerifyOtpRequest request);
    void resetPassword(ResetPasswordRequest request);
}
