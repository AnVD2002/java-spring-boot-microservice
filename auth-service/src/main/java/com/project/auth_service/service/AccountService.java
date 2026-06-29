package com.project.auth_service.service;

import com.project.auth_service.dto.response.AccountInfoDto;
import com.project.auth_service.entity.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountService {

    Account getAccountByEmail(String email);

    Optional<Account> getAccountExistedByEmail(String email);

    Optional<Account> getAnyNonDeletedAccountByEmail(String email);

    AccountInfoDto getAccountInfoDtoByUsername(String username);

    AccountInfoDto getAccountInfoDtoByEmail(String email);

    void deleteAccountById(UUID id);

    Account saveAccount(Account account);
}
