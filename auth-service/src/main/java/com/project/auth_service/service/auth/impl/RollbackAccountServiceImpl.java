package com.project.auth_service.service.auth.impl;

import com.project.auth_service.dto.event.AccountCreationFailedEvent;
import com.project.auth_service.repository.AccountRepository;
import com.project.auth_service.service.auth.RollbackAccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RollbackAccountServiceImpl implements RollbackAccountService {
    private final AccountRepository accountRepository;

    @Transactional
    public void rollbackAccount(AccountCreationFailedEvent event) {
        accountRepository.deleteAccountById(event.getAccountId());
    }
}
