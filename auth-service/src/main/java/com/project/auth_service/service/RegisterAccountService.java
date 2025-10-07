package com.project.auth_service.service;

import com.project.auth_service.dto.request.AccountRegistrationRequest;

public interface RegisterAccountService {
    void RegisterAccount(AccountRegistrationRequest request);
}
