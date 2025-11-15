package com.project.auth_service.service;

import com.project.auth_service.dto.response.AccountInfoDto;
import com.project.auth_service.dto.response.GoogleUserInfo;
import com.project.auth_service.dto.response.LoginResponse;
import com.project.auth_service.repository.AccountRepository;
import com.project.common_lib_service.config.JwtProvider;

import com.project.common_lib_service.exception.AuthenticationError;
import com.project.common_lib_service.exception.AuthenticationException;
import com.project.common_lib_service.exception.SystemError;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class LoginOauth2ServiceImpl implements LoginOAuth2Service {

    private final GoogleOAuth2Service googleOAuth2Service;

    private final LoginDefaultService loginDefaultService;

    private final JwtProvider jwtProvider;

    private final AccountRepository accountRepository;

    @Override
    public LoginResponse loginOauth2(String accessToken) {

        try {
            GoogleUserInfo googleUserInfo = googleOAuth2Service.getGoogleUserInfo(accessToken);

            String email = googleUserInfo.getEmail();

            if (ObjectUtils.isEmpty(email)) {
                throw new AuthenticationException(AuthenticationError.ERROR_001, "Email not found in Google account");
            }

            AccountInfoDto accountInfoDto = accountRepository.getAccountInfoDtoByEmail(email);

            if (ObjectUtils.isEmpty(accountInfoDto)) {
                throw new AuthenticationException(AuthenticationError.ERROR_002, "No account associated with this email");
            }

            String username = accountInfoDto.getUsername();

            UUID accountId = accountInfoDto.getId();

            List<String> roles = loginDefaultService.getListRoleByEmail(email);

            String newAccessToken = jwtProvider.generateAccessToken(username, accountId, roles);

            String newRefreshToken = jwtProvider.generateRefreshToken(username, accountId, roles);

            return LoginResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .username(username)
                    .email(email)
                    .roles(roles)
                    .build();
        } catch (Exception e) {
            if (e instanceof AuthenticationException) {
                throw e;
            } else {
                throw new AuthenticationException(SystemError.ERROR_500, "Failed to login with Google account");
            }
        }
    }
}
