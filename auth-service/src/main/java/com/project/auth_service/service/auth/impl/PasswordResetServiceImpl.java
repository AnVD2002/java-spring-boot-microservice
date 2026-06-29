package com.project.auth_service.service.auth.impl;

import com.project.auth_service.config.OtpTopicProperties;
import com.project.auth_service.dto.request.ForgotPasswordRequest;
import com.project.auth_service.dto.request.ResetPasswordRequest;
import com.project.auth_service.dto.request.VerifyOtpRequest;
import com.project.auth_service.dto.response.VerifyOtpResponse;
import com.project.auth_service.entity.Account;
import com.project.auth_service.service.AccountService;
import com.project.auth_service.service.auth.PasswordResetService;
import com.project.common_lib_service.event.PasswordResetEmailEvent;
import com.project.common_lib_service.exception.AuthenticationError;
import com.project.common_lib_service.exception.SystemError;
import com.project.common_lib_service.exception.SystemException;
import com.project.common_lib_service.repository.RefreshTokenRepository;
import com.project.common_lib_service.service.AuditLogService;
import com.project.common_lib_service.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.project.auth_service.utils.SystemUtils.generateOtp;
import static com.project.auth_service.enums.AuditAction.UPDATE;
import static com.project.auth_service.enums.AuditEntityType.ACCOUNT_PASSWORD;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final AccountService accountService;
    private final StringRedisTemplate redisTemplate;
    private final KafkaProducerService kafkaProducerService;
    private final OtpTopicProperties otpTopicProperties;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuditLogService auditLogService;

    private static final String OTP_KEY_PREFIX = "OTP:";
    private static final String OTP_ATTEMPT_PREFIX = "OTP_ATTEMPT:";
    private static final String RESET_TOKEN_KEY_PREFIX = "RESET_TOKEN:";
    private static final long OTP_TTL_MINUTES = 5;
    private static final long RESET_TOKEN_TTL_MINUTES = 15;
    private static final int MAX_OTP_ATTEMPTS = 5;

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail();

        // Silently ignore if email not found — prevents user enumeration
        try {
            accountService.getAccountByEmail(email);
        } catch (Exception e) {
            log.debug("Forgot password requested for non-existent email: {}", email);
            return;
        }

        String otp = generateOtp();
        redisTemplate.opsForValue().set(OTP_KEY_PREFIX + email, otp, OTP_TTL_MINUTES, TimeUnit.MINUTES);

        // Reset attempt counter when new OTP is issued
        redisTemplate.delete(OTP_ATTEMPT_PREFIX + email);

        PasswordResetEmailEvent event = PasswordResetEmailEvent.builder()
                .email(email)
                .otp(otp)
                .build();

        kafkaProducerService.sendMessage(otpTopicProperties.getSendOtpEmail(), email, event);
    }

    @Override
    public VerifyOtpResponse verifyOtp(VerifyOtpRequest request) {
        String email = request.getEmail();
        String otpKey = OTP_KEY_PREFIX + email;
        String attemptKey = OTP_ATTEMPT_PREFIX + email;

        // Brute-force protection
        String attemptStr = redisTemplate.opsForValue().get(attemptKey);
        int attempts = attemptStr != null ? Integer.parseInt(attemptStr) : 0;
        if (attempts >= MAX_OTP_ATTEMPTS) {
            throw new SystemException(AuthenticationError.AUTH_004);
        }

        String storedOtp = redisTemplate.opsForValue().get(otpKey);
        if (storedOtp == null || !storedOtp.equals(request.getOtp())) {
            Long count = redisTemplate.opsForValue().increment(attemptKey);
            if (count != null && count == 1) {
                redisTemplate.expire(attemptKey, OTP_TTL_MINUTES, TimeUnit.MINUTES);
            }
            throw new SystemException(AuthenticationError.AUTH_005);
        }

        // OTP valid — clean up
        redisTemplate.delete(otpKey);
        redisTemplate.delete(attemptKey);

        String resetToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                RESET_TOKEN_KEY_PREFIX + resetToken, email, RESET_TOKEN_TTL_MINUTES, TimeUnit.MINUTES);

        return VerifyOtpResponse.builder().resetToken(resetToken).build();
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String tokenKey = RESET_TOKEN_KEY_PREFIX + request.getResetToken();
        String email = redisTemplate.opsForValue().get(tokenKey);

        if (email == null) {
            throw new SystemException(AuthenticationError.AUTH_006);
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new SystemException(SystemError.ERROR_027);
        }

        Account account = accountService.getAccountByEmail(email);
        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        Account saved = accountService.saveAccount(account);
        auditLogService.record(
                UPDATE,
                ACCOUNT_PASSWORD,
                saved.getId(),
                null,
                java.util.Map.of("passwordChanged", true)
        );

        refreshTokenRepository.deleteAllByAccountId(account.getId());
        redisTemplate.delete(tokenKey);
    }
}
