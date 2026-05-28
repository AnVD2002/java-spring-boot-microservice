package com.project.auth_service.infrastructure.message;

import com.project.auth_service.config.UserCommandTopicProperties;
import com.project.auth_service.dto.event.CreateUserCommand;
import com.project.auth_service.entity.Account;
import com.project.common_lib_service.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaAccountEventPublisher implements AccountEventPublisher {

    private final KafkaProducerService kafkaProducerService;
    private final UserCommandTopicProperties topicProperties;

    @Override
    public void publishAccountCreated(Account account) {
        CreateUserCommand command = CreateUserCommand.builder()
                .accountId(account.getId())
                .email(account.getEmail())
                .username(account.getUsername())
                .build();
        try {
            kafkaProducerService.sendMessage(topicProperties.getCreate(), account.getId().toString(), command);
        } catch (Exception e) {
            log.warn("Failed to publish AccountCreated event for accountId={}: {}", account.getId(), e.getMessage());
        }
    }
}
