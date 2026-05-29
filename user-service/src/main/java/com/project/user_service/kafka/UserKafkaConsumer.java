package com.project.user_service.kafka;

import com.project.common_lib_service.kafka.DlqHandler;
import com.project.user_service.dto.AccountCreatedEvent;
import com.project.user_service.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserKafkaConsumer {

    private final UserServiceImpl userService;
    private final DlqHandler dlqHandler;

    @KafkaListener(
            topics = "${kafka.topic.create-user}",
            groupId = "user-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleAccountCreated(AccountCreatedEvent event) {
        log.info("Received AccountCreatedEvent for accountId={}", event.getAccountId());
        userService.createUser(event);
        log.info("User created successfully for accountId={}", event.getAccountId());
    }

    @KafkaListener(
            topics = "${kafka.topic.create-user}.DLT",
            groupId = "user-service-dlq-group"
    )
    public void handleDLQ(ConsumerRecord<String, String> record) {
        dlqHandler.handle(record);
    }
}
