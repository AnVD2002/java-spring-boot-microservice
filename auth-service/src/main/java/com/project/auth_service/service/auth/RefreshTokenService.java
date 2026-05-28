package com.project.auth_service.service.auth;

import com.project.auth_service.dto.response.LoginResponse;

public interface RefreshTokenService {
    LoginResponse refreshToken(String refreshToken);
    void logout(String refreshToken);
}
