package com.project.auth_service.service;

import com.project.auth_service.dto.request.AccountRegistrationRequest;
import com.project.auth_service.dto.response.GoogleUserInfo;
import com.project.auth_service.dto.event.AccountCreatedEvent;
import com.project.auth_service.entity.Account;
import com.project.auth_service.repository.AccountRepository;
import com.project.common_lib_service.exception.SystemError;
import com.project.common_lib_service.exception.SystemException;
import com.project.common_lib_service.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegisterAccountServiceImpl implements RegisterAccountService {

    private final GoogleOAuth2Service googleOAuth2Service;

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    private final KafkaProducerService kafkaProducerService;

    /**
     * Register a new account using Google OAuth2 information.
     *
     * @param request the account registration request containing Google token, username, password, and confirmPassword
     * @throws SystemException if Google token is invalid, email already exists, or password mismatch
     */
    public void RegisterAccount(AccountRegistrationRequest request) {

        // 1. Fetch Google user info using the token
        GoogleUserInfo googleUserInfo = googleOAuth2Service.getGoogleUserInfo(request.getToken());

        // If no user info is returned => throw exception
        if (ObjectUtils.isEmpty(googleUserInfo)) {
            throw new SystemException(SystemError.ERROR_029); // Failed to validate Google token
        }

        String email = googleUserInfo.getEmail();

        // 2. Check if the email already exists in the system
        Optional<Account> accountExisted = accountRepository.getAccountExisted(email);
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

        accountRepository.save(account);

        // 5. Publish account created event to Kafka so other services can handle follow-up tasks
        AccountCreatedEvent accountCreatedEvent = AccountCreatedEvent.builder()
                .accountId(account.getId())
                .email(email)
                .username(account.getUsername())
                .build();

        kafkaProducerService.sendMessage("account-created-topic", accountCreatedEvent);
    }

}
