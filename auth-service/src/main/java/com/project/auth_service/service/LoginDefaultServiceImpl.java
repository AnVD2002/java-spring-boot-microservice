package com.project.auth_service.service;

import com.project.auth_service.dto.request.LoginDefaultRequest;
import com.project.auth_service.dto.response.AccountInfoDto;
import com.project.auth_service.dto.response.LoginResponse;
import com.project.auth_service.repository.AccountRepository;
import com.project.auth_service.repository.RoleRepository;
import com.project.common_lib_service.config.JwtProperties;
import com.project.common_lib_service.config.JwtProvider;
import com.project.common_lib_service.exception.AuthenticationError;
import com.project.common_lib_service.exception.SystemException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
@Service
@Transactional
public class LoginDefaultServiceImpl implements LoginDefaultService {

    private final JwtProvider jwtProvider;

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    private final StringRedisTemplate redisTemplate;

    private final DeviceService deviceService;

    private final JwtProperties jwtProperties;

    private static final String LOGIN_FAIL_KEY = "LOGIN_FAIL:"; // key prefix

    private static final String REFRESH_TOKEN_KEY = "REFRESH_TOKEN:"; // key prefix

    private final RoleRepository roleRepository;


    /**
     * Default login method
     *
     * @param loginDefaultRequest
     * @param deviceIdRequest
     * @param accountIdRequest
     * @return
     */
    public LoginResponse loginDefault(LoginDefaultRequest loginDefaultRequest, String deviceIdRequest, String accountIdRequest) {

        String username = loginDefaultRequest.getUsername();
        String rawPassword = loginDefaultRequest.getPassword();

        // 1. Rate limiting check
        String failKey = LOGIN_FAIL_KEY + username;
        String failCount = redisTemplate.opsForValue().get(failKey);
        if (failCount != null && Integer.parseInt(failCount) >= 5) {
            throw new SystemException(AuthenticationError.ERROR_004); // Too many login attempts
        }

        AccountInfoDto accountInfoDto = accountRepository.getAccountInfoDtoByUsername(username);
        if (ObjectUtils.isEmpty(accountInfoDto)) {
            increaseFailCount(failKey);
            throw new SystemException(AuthenticationError.ERROR_002); // User not found
        }

        if (!passwordEncoder.matches(rawPassword, accountInfoDto.getPassword())) {
            increaseFailCount(failKey);
            throw new SystemException(AuthenticationError.ERROR_001); // Invalid password
        }

        // 2. Reset fail count on success
        redisTemplate.delete(failKey);

        // 3. Generate tokens
        List<String> roles = getListRoleByUsername(username);

        if (CollectionUtils.isEmpty(roles)) {
            throw new SystemException(AuthenticationError.ERROR_003); // No roles
        }

        // Generate access tokens
        String accessToken = jwtProvider.generateAccessToken(username, accountInfoDto.getId(), roles);

        // Generate refresh token
        String refreshToken = jwtProvider.generateRefreshToken(username, accountInfoDto.getId(), roles);

        // 4. Save refresh token in Redis (with TTL)
        long refreshTokenTtl = jwtProperties.getRefreshExpiration();

        // after getOrCreate deviceId
        String deviceId = deviceService.getAndSaveDeviceId(deviceIdRequest, accountIdRequest);

        // Save refresh token with deviceId
        String refreshTokenKey = REFRESH_TOKEN_KEY + accountInfoDto.getId() + ":" + deviceId;

        // If already have refresh token for this device, overwrite it
        redisTemplate.opsForValue().set(
                refreshTokenKey,
                refreshToken,
                refreshTokenTtl,
                TimeUnit.MILLISECONDS
        );

        // 5. Save deviceId to set of devices for this account
        String devicesKey = REFRESH_TOKEN_KEY + "DEVICES:" + accountInfoDto.getId();
        redisTemplate.opsForSet().add(devicesKey, deviceId);
        redisTemplate.expire(devicesKey, refreshTokenTtl, TimeUnit.MILLISECONDS);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(accountInfoDto.getEmail())
                .roles(roles)
                .build();
    }

    /**
     * Increase fail count in Redis
     *
     * @param failKey
     */
    private void increaseFailCount(String failKey) {
        Long count = redisTemplate.opsForValue().increment(failKey);
        if (Objects.equals(count, 1L)) {
            redisTemplate.expire(failKey, 5, TimeUnit.MINUTES);
        }

    }

    /**
     * Get list of roles for a username
     *
     * @param username
     * @return
     */
    public List<String> getListRoleByUsername(String username) {
        List<String> roles = roleRepository.getRoleNameByUsername(username);
        if (CollectionUtils.isEmpty(roles)) {
            throw new SystemException(AuthenticationError.ERROR_003); // No roles
        }
        return roles;
    }

    /**
     * Get list of roles for a username
     *
     * @param email
     * @return
     */
    public List<String> getListRoleByEmail(String email) {
        List<String> roles = roleRepository.getRoleNameByEmail(email);
        if (CollectionUtils.isEmpty(roles)) {
            throw new SystemException(AuthenticationError.ERROR_003); // No roles
        }
        return roles;
    }


}
