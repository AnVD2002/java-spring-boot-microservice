package com.project.common_lib_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Synchronous send
    public void sendMessage(String topic, Object message) {
        kafkaTemplate.send(topic, message);
    }

    // Synchronous send with key
    public void sendMessage(String topic, String key, Object message) {
        kafkaTemplate.send(topic, key, message);
    }

    // Asynchronous send
    public CompletableFuture<SendResult<String, Object>> sendMessageAsync(String topic, Object message) {
        return kafkaTemplate.send(topic, message);
    }

    // Asynchronous send with key
    public CompletableFuture<SendResult<String, Object>> sendMessageAsync(String topic, String key, Object message) {
        return kafkaTemplate.send(topic, key, message);
    }
}
