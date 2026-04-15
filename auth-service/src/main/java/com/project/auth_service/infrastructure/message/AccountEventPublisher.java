package com.project.auth_service.infrastructure.message;

import com.project.auth_service.entity.Account;

public interface AccountEventPublisher {
    void publishAccountCreated(Account account);
}
