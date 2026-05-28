package com.project.user_service.kafka;

import com.project.user_service.dto.AccountCreatedEvent;
import com.project.user_service.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserKafkaConsumer {

    private final UserServiceImpl userService;

    @KafkaListener(
            topics = "${kafka.topic.create-user}",
            groupId = "user-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleAccountCreated(AccountCreatedEvent event) {
        log.info("Received AccountCreatedEvent for accountId={}, email={}", event.getAccountId(), event.getEmail());
        try {
            userService.createUser(event);
            log.info("User created successfully for accountId={}", event.getAccountId());
        } catch (Exception e) {
            log.error("Failed to create user for accountId={}: {}", event.getAccountId(), e.getMessage(), e);
        }
    }
}
