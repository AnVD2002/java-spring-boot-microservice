package com.project.auth_service.service.auth.impl;


import com.project.auth_service.service.auth.RefreshTokenService;
import com.project.common_lib_service.jwt.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final JwtProvider jwtProvider;

    @Override
    public String refreshToken(String refreshToken) {
        String username = jwtProvider.extractUserName(refreshToken);

        UUID accountId = jwtProvider.extractAccountId(refreshToken);

        List<String> roles = jwtProvider.extractUserRole(refreshToken);

        return jwtProvider.generateAccessToken(username, accountId, roles);

    }

}
