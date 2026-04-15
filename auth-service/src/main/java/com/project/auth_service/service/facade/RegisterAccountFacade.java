package com.project.auth_service.service.facade;

import com.project.auth_service.dto.request.AccountRegistrationRequest;
import com.project.auth_service.entity.Account;
import com.project.auth_service.infrastructure.message.AccountEventPublisher;
import com.project.auth_service.service.auth.RegisterAccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterAccountFacade {

    private final RegisterAccountService registerAccountService;
    private final AccountEventPublisher accountEventPublisher;

    @Transactional
    public void register(AccountRegistrationRequest request) {
        Account account = registerAccountService.registerAccount(request);
        accountEventPublisher.publishAccountCreated(account);
    }
}
