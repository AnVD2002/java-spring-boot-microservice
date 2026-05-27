package com.project.auth_service.service.auth.impl;

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
    public String refreshToken(String refreshToken) {
        if (!jwtProvider.isSignatureValid(refreshToken) || jwtProvider.isTokenExpired(refreshToken)) {
            throw new SystemException(SystemError.ERROR_019); // Token expired
        }

        String jti = jwtProvider.extractJti(refreshToken);
        if (jti == null) {
            throw new SystemException(SystemError.ERROR_019);
        }

        RefreshTokenInfo tokenInfo = refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> new SystemException(SystemError.ERROR_019)); // revoked or not found

        // Revoke used token — prevents replay attacks
        refreshTokenRepository.delete(jti);

        return jwtProvider.generateAccessToken(
                tokenInfo.getUsername(),
                tokenInfo.getAccountId(),
                tokenInfo.getRoles()
        );
    }
}
