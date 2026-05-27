package com.project.notification_service.consumer;

import com.project.common_lib_service.event.PasswordResetEmailEvent;
import com.project.notification_service.kafka.consumer.RegistrationEmailConfirmedEvent;
import com.project.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OtpEmailConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "${kafka.notification.send-otp-email-topic:send-otp-email-topic}", groupId = "${spring.kafka.consumer.group-id:notification-service-group}")
    public void consumeOtpEmail(PasswordResetEmailEvent event) {
        log.info("Received OTP email event for: {}", event.getEmail());
        try {
            emailService.sendOtpEmail(event.getEmail(), event.getOtp());
            log.info("OTP email sent to: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", event.getEmail(), e.getMessage());
        }
    }

    @KafkaListener(topics = "${kafka.notification.send-confirmation-registration-email}", groupId = "${spring.kafka.consumer.group-id:notification-service-group}")
    public void consumeOtpEmailRegistration(RegistrationEmailConfirmedEvent event) {
        log.info("Received OTP email event registration for: {}", event.getEmail());
        try {
            emailService.sendOtpEmail(event.getEmail(), event.getOtp());
            log.info("OTP email registration sent to: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Failed to send OTP email registration to {}: {}", event.getEmail(), e.getMessage());
        }
    }
}
