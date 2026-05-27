package com.project.auth_service.service.auth.impl;

import com.project.auth_service.config.OtpTopicProperties;
import com.project.auth_service.dto.request.AccountRegistrationRequest;
import com.project.auth_service.dto.request.AccountRegistrationRequestNormal;
import com.project.auth_service.dto.response.GoogleUserInfo;
import com.project.auth_service.entity.Account;
import com.project.auth_service.kafka.producer.RegistrationEmailConfirmedEvent;
import com.project.auth_service.service.AccountService;
import com.project.auth_service.service.auth.RegisterAccountService;
import com.project.auth_service.service.provider.GoogleOAuth2Service;
import com.project.common_lib_service.exception.SystemError;
import com.project.common_lib_service.exception.SystemException;
import com.project.common_lib_service.service.KafkaProducerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

import static com.project.auth_service.utils.SystemUtils.generateOtp;

@Service
@RequiredArgsConstructor
public class RegisterAccountServiceImpl implements RegisterAccountService {

    private final GoogleOAuth2Service googleOAuth2Service;

    private final PasswordEncoder passwordEncoder;

    private final AccountService accountService;

    private final OtpTopicProperties otpTopicProperties;

    private final KafkaProducerService KafkaProducerService;

    /**
     * Register a new account using Google OAuth2 information.
     *
     * @param request the account registration request containing Google token, username, password, and confirmPassword
     * @throws SystemException if Google token is invalid, email already exists, or password mismatch
     */
    @Transactional
    public Account registerAccount(AccountRegistrationRequest request) {

        // 1. Fetch Google user info using the token
        GoogleUserInfo googleUserInfo = googleOAuth2Service.verifyAndDecode(request.getTokenId());

        // If no user info is returned => throw exception
        if (ObjectUtils.isEmpty(googleUserInfo)) {
            throw new SystemException(SystemError.ERROR_029); // Failed to validate Google token
        }

        String email = googleUserInfo.getEmail();

        // 2. Check if the email already exists in the system
        Optional<Account> accountExisted = accountService.getAccountExistedByEmail(email);
        if (accountExisted.isPresent()) {
            throw new SystemException(SystemError.ERROR_028); // Email already registered
        }

        // 3. Ensure password and confirm password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new SystemException(SystemError.ERROR_027); // Password mismatch
        }

        // 4. Create a new account entity
        Account account = Account.builder()
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .username(request.getUsername())
                .status(1) // 1 = active
                .build();

        accountService.saveAccount(account);

        return account;

    }

    @Transactional
    public Account registerAccountNormal(AccountRegistrationRequestNormal request) {
        // 1. Check if the email already exists in the system
        Optional<Account> accountExisted = accountService.getAccountExistedByEmail(request.getEmail());
        if (accountExisted.isPresent()) {
            throw new SystemException(SystemError.ERROR_028); // Email already registered
        }

        // 2. Ensure password and confirm password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new SystemException(SystemError.ERROR_027); // Password mismatch
        }

        // 3. Create a new account entity
        Account account = Account.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .username(request.getUsername())
                .status(1)
                .build();

        accountService.saveAccount(account);

        String otp = generateOtp();

        RegistrationEmailConfirmedEvent event = RegistrationEmailConfirmedEvent.builder()
                .email(request.getEmail())
                .otp(otp)
                .build();

         KafkaProducerService.sendMessage(otpTopicProperties.getSendConfirmationRegistrationEmail(), request.getEmail(), event);

        return account;

    }

}
