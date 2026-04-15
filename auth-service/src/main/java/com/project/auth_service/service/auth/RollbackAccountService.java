package com.project.auth_service.service.auth;

import com.project.auth_service.dto.event.AccountCreationFailedEvent;

public interface RollbackAccountService {
    void rollbackAccount(AccountCreationFailedEvent event);
}
