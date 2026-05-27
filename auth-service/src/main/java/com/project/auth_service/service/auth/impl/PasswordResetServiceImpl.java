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
import com.project.common_lib_service.exception.SystemException;
import com.project.common_lib_service.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.project.auth_service.utils.SystemUtils.generateOtp;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final AccountService accountService;
    private final StringRedisTemplate redisTemplate;
    private final KafkaProducerService kafkaProducerService;
    private final OtpTopicProperties otpTopicProperties;
    private final PasswordEncoder passwordEncoder;

    private static final String OTP_KEY_PREFIX = "OTP:";
    private static final String RESET_TOKEN_KEY_PREFIX = "RESET_TOKEN:";
    private static final long OTP_TTL_MINUTES = 5;
    private static final long RESET_TOKEN_TTL_MINUTES = 15;

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail();

        accountService.getAccountByEmail(email);

        String otp = generateOtp();
        redisTemplate.opsForValue().set(OTP_KEY_PREFIX + email, otp, OTP_TTL_MINUTES, TimeUnit.MINUTES);

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

        String storedOtp = redisTemplate.opsForValue().get(otpKey);
        if (storedOtp == null || !storedOtp.equals(request.getOtp())) {
            throw new SystemException(AuthenticationError.AUTH_001);
        }

        redisTemplate.delete(otpKey);

        String resetToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(RESET_TOKEN_KEY_PREFIX + resetToken, email, RESET_TOKEN_TTL_MINUTES, TimeUnit.MINUTES);

        return VerifyOtpResponse.builder().resetToken(resetToken).build();
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String tokenKey = RESET_TOKEN_KEY_PREFIX + request.getResetToken();
        String email = redisTemplate.opsForValue().get(tokenKey);

        if (email == null) {
            throw new SystemException(AuthenticationError.AUTH_001);
        }

        Account account = accountService.getAccountByEmail(email);

        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountService.saveAccount(account);

        redisTemplate.delete(tokenKey);
    }
}
