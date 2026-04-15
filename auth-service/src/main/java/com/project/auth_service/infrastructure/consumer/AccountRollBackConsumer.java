package com.project.auth_service.infrastructure.consumer;

import com.project.auth_service.dto.event.AccountCreationFailedEvent;
import com.project.auth_service.service.auth.RollbackAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountRollBackConsumer {

    private final RollbackAccountService rollbackAccountService;

    @KafkaListener(
            topics = "user-creation-failed",
            groupId = "auth-service"
    )
    public void rollback(AccountCreationFailedEvent event) {
        rollbackAccountService.rollbackAccount(event);
    }
}
