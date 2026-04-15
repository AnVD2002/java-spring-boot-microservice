package com.project.auth_service.infrastructure.message;

import com.project.auth_service.config.UserCommandTopicProperties;
import com.project.auth_service.dto.event.CreateUserCommand;
import com.project.auth_service.entity.Account;
import com.project.common_lib_service.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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

        kafkaProducerService.sendMessage(
                topicProperties.getCreate(),
                account.getId().toString(),
                command
        );
    }
}
