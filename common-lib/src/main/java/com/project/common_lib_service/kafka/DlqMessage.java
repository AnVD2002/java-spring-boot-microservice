package com.project.common_lib_service.kafka;

import lombok.Builder;

import java.time.Instant;

@Builder
public record DlqMessage(String originalTopic, int originalPartition, long originalOffset, String exceptionClass,
                         String exceptionMessage, String payload, Instant failedAt) {
}
