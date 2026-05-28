package com.project.auth_service.service.impl;

import com.project.auth_service.dto.response.AccountInfoDto;
import com.project.auth_service.entity.Account;
import com.project.auth_service.repository.AccountRepository;
import com.project.auth_service.service.AccountService;
import com.project.common_lib_service.exception.AuthenticationError;
import com.project.common_lib_service.exception.SystemException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public Account getAccountByEmail(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new SystemException(AuthenticationError.AUTH_002));
    }

    @Override
    public Optional<Account> getAccountExistedByEmail(String email) {
        return accountRepository.getAccountExisted(email);
    }

    @Override
    public AccountInfoDto getAccountInfoDtoByUsername(String username) {
        return accountRepository.getAccountInfoDtoByUsername(username);
    }

    @Override
    public AccountInfoDto getAccountInfoDtoByEmail(String email) {
        return accountRepository.getAccountInfoDtoByEmail(email);
    }

    @Override
    public void deleteAccountById(UUID id) {
        accountRepository.deleteAccountById(id);
    }

    @Override
    public void saveAccount(Account account) {
        accountRepository.save(account);
    }
}
