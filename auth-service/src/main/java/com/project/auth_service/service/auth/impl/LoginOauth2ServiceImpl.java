package com.project.auth_service.service.auth.impl;

import com.project.auth_service.config.AuthTokenProperties;
import com.project.auth_service.dto.request.LoginGoogleRequest;
import com.project.auth_service.dto.response.AccountInfoDto;
import com.project.auth_service.dto.response.GoogleUserInfo;
import com.project.auth_service.dto.response.LoginResponse;
import com.project.auth_service.repository.AccountRepository;
import com.project.auth_service.service.provider.GoogleOAuth2Service;
import com.project.auth_service.service.auth.LoginDefaultService;
import com.project.auth_service.service.auth.LoginOAuth2Service;
import com.project.common_lib_service.jwt.JwtProvider;
import com.project.common_lib_service.exception.AuthenticationError;
import com.project.common_lib_service.exception.AuthenticationException;
import com.project.common_lib_service.exception.SystemError;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class LoginOauth2ServiceImpl implements LoginOAuth2Service {

    private final GoogleOAuth2Service googleOAuth2Service;

    private final LoginDefaultService loginDefaultService;

    private final JwtProvider jwtProvider;

    private final AccountRepository accountRepository;

    private final RedisTemplate<String, String> redisTemplate;

    private final AuthTokenProperties authTokenProperties;

    @Override
    public LoginResponse loginOauth2(LoginGoogleRequest loginGoogleRequest) {

        try {
            GoogleUserInfo googleUserInfo = googleOAuth2Service.getGoogleUserInfo(loginGoogleRequest.getToken());

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

            String refreshToken = UUID.randomUUID().toString();

            String redisKey =
                    authTokenProperties.getRefresh().getRedisPrefix() + refreshToken;

            redisTemplate.opsForValue().set(
                    redisKey,
                    accountId.toString(),
                    authTokenProperties.getRefresh().getTtlDays(),
                    TimeUnit.DAYS);

            return LoginResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken)
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
