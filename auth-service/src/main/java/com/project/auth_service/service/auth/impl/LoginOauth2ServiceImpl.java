package com.project.auth_service.service.auth.impl;

import com.project.auth_service.dto.request.LoginGoogleRequest;
import com.project.auth_service.dto.response.AccountInfoDto;
import com.project.auth_service.dto.response.GoogleUserInfo;
import com.project.auth_service.dto.response.LoginResponse;
import com.project.auth_service.service.AccountService;
import com.project.auth_service.service.device.DeviceService;
import com.project.auth_service.service.provider.GoogleOAuth2Service;
import com.project.auth_service.service.auth.LoginDefaultService;
import com.project.auth_service.service.auth.LoginOAuth2Service;
import com.project.common_lib_service.jwt.JwtProvider;
import com.project.common_lib_service.exception.AuthenticationError;
import com.project.common_lib_service.exception.AuthenticationException;
import com.project.common_lib_service.exception.BaseException;
import com.project.common_lib_service.exception.SystemError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class LoginOauth2ServiceImpl implements LoginOAuth2Service {

    private final GoogleOAuth2Service googleOAuth2Service;

    private final LoginDefaultService loginDefaultService;

    private final JwtProvider jwtProvider;

    private final AccountService accountService;

    private final DeviceService deviceService;

    @Override
    public LoginResponse loginOauth2(LoginGoogleRequest loginGoogleRequest, String deviceId) {

        try {
            GoogleUserInfo googleUserInfo = googleOAuth2Service.verifyAndDecode(loginGoogleRequest.getToken());

            String email = googleUserInfo.getEmail();

            if (ObjectUtils.isEmpty(email)) {
                throw new AuthenticationException(AuthenticationError.AUTH_001, "Email not found in Google account");
            }

            AccountInfoDto accountInfoDto = accountService.getAccountInfoDtoByEmail(email);

            if (ObjectUtils.isEmpty(accountInfoDto)) {
                throw new AuthenticationException(AuthenticationError.AUTH_002, "No account associated with this email");
            }

            String username = accountInfoDto.getUsername();

            UUID accountId = accountInfoDto.getId();

            List<String> roles = loginDefaultService.getListRoleByEmail(email);

            String newAccessToken = jwtProvider.generateAccessToken(username, accountId, roles);

            String refreshToken = jwtProvider.generateRefreshToken(username, accountId, roles);

            UUID resolvedDeviceId = deviceService.getAndSaveDeviceId(deviceId, accountId);

            return LoginResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken)
                    .deviceId(resolvedDeviceId)
                    .username(username)
                    .email(email)
                    .roles(roles)
                    .build();
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthenticationException(SystemError.ERROR_500, "Failed to login with Google account");
        }
    }
}
