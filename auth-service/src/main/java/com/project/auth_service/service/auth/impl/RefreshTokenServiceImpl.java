package com.project.auth_service.service.auth.impl;

import com.project.auth_service.dto.response.LoginResponse;
import com.project.auth_service.service.auth.RefreshTokenService;
import com.project.common_lib_service.dto.RefreshTokenInfo;
import com.project.common_lib_service.exception.SystemError;
import com.project.common_lib_service.exception.SystemException;
import com.project.common_lib_service.jwt.JwtProvider;
import com.project.common_lib_service.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        RefreshTokenInfo tokenInfo = validateAndExtract(refreshToken);

        // Revoke used token — prevents replay attacks
        refreshTokenRepository.delete(tokenInfo.getJti());

        // Issue new access token + new refresh token (rotation)
        String newAccessToken = jwtProvider.generateAccessToken(
                tokenInfo.getUsername(), tokenInfo.getAccountId(), tokenInfo.getRoles());

        String newRefreshToken = jwtProvider.generateRefreshToken(
                tokenInfo.getUsername(), tokenInfo.getAccountId(), tokenInfo.getRoles());

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .username(tokenInfo.getUsername())
                .roles(tokenInfo.getRoles())
                .build();
    }

    @Override
    public void logout(String refreshToken) {
        RefreshTokenInfo tokenInfo = validateAndExtract(refreshToken);
        refreshTokenRepository.delete(tokenInfo.getJti());
    }

    private RefreshTokenInfo validateAndExtract(String refreshToken) {
        if (!jwtProvider.isSignatureValid(refreshToken) || jwtProvider.isTokenExpired(refreshToken)) {
            throw new SystemException(SystemError.ERROR_019);
        }

        String jti = jwtProvider.extractJti(refreshToken);
        if (jti == null) {
            throw new SystemException(SystemError.ERROR_019);
        }

        return refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> new SystemException(SystemError.ERROR_019)); // revoked or not found
    }
}
