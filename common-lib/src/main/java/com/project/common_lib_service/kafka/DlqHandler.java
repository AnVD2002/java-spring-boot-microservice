package com.project.common_lib_service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.function.Consumer;

@Slf4j
@Component
public class DlqHandler {

    public void handle(ConsumerRecord<String, ?> record) {
        handle(record, null);
    }

    public void handle(ConsumerRecord<String, ?> record, Consumer<DlqMessage> onFailed) {
        DlqMessage message = parse(record);
        log.error("[DLQ] topic={} partition={} offset={} exception={} | payload={}",
                message.originalTopic(),
                message.originalPartition(),
                message.originalOffset(),
                message.exceptionMessage(),
                message.payload());
        if (onFailed != null) {
            onFailed.accept(message);
        }
    }

    private DlqMessage parse(ConsumerRecord<String, ?> record) {
        return DlqMessage.builder()
                .originalTopic(getHeader(record, "kafka_dlt-original-topic"))
                .originalPartition(parseIntHeader(record, "kafka_dlt-original-partition"))
                .originalOffset(parseLongHeader(record, "kafka_dlt-original-offset"))
                .exceptionClass(getHeader(record, "kafka_dlt-exception-fqcn"))
                .exceptionMessage(getHeader(record, "kafka_dlt-exception-message"))
                .payload(record.value() != null ? record.value().toString() : null)
                .failedAt(Instant.now())
                .build();
    }

    private String getHeader(ConsumerRecord<?, ?> record, String key) {
        Header header = record.headers().lastHeader(key);
        return header != null ? new String(header.value()) : "unknown";
    }

    private int parseIntHeader(ConsumerRecord<?, ?> record, String key) {
        Header header = record.headers().lastHeader(key);
        return header != null ? ByteBuffer.wrap(header.value()).getInt() : -1;
    }

    private long parseLongHeader(ConsumerRecord<?, ?> record, String key) {
        Header header = record.headers().lastHeader(key);
        return header != null ? ByteBuffer.wrap(header.value()).getLong() : -1L;
    }
}
