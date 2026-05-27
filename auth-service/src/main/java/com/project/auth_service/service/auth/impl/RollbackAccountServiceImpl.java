package com.project.auth_service.service.auth.impl;

import com.project.auth_service.dto.event.AccountCreationFailedEvent;
import com.project.auth_service.service.AccountService;
import com.project.auth_service.service.auth.RollbackAccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RollbackAccountServiceImpl implements RollbackAccountService {
    private final AccountService accountService;

    @Transactional
    public void rollbackAccount(AccountCreationFailedEvent event) {
        accountService.deleteAccountById(event.getAccountId());
    }
}
