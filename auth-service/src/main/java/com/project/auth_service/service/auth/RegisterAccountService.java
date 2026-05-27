package com.project.auth_service.service.auth;

import com.project.auth_service.dto.request.AccountRegistrationRequest;
import com.project.auth_service.dto.request.AccountRegistrationRequestNormal;
import com.project.auth_service.entity.Account;

public interface RegisterAccountService {
    Account registerAccount(AccountRegistrationRequest request);

    Account registerAccountNormal(AccountRegistrationRequestNormal request);
}
