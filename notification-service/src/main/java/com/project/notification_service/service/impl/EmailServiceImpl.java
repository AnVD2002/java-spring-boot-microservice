package com.project.notification_service.service.impl;

import com.project.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final List<String> ADDRESS_NOT_FOUND_MARKERS = List.of(
            "address not found",
            "no such user",
            "recipient address rejected",
            "user unknown",
            "550",
            "553"
    );

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Password Reset OTP");
        message.setText(
                "Your OTP code for password reset is: " + otp + "\n\n" +
                "This code expires in 5 minutes.\n" +
                "If you did not request this, please ignore this email."
        );

        try {
            mailSender.send(message);
        } catch (MailSendException e) {
            if (isAddressNotFound(e)) {
                // Recipient address does not exist on the mail server (e.g. "550 Address not found").
                // Retrying this message will never succeed, so we log and stop instead of letting it
                // blow up the Kafka listener and end up looping through the DLQ.
                log.warn("Email address not found, skip sending OTP email: to={}, reason={}", to, e.getMessage());
                return;
            }
            throw e;
        }
    }

    private boolean isAddressNotFound(MailSendException e) {
        String message = e.getMessage();
        if (message == null) {
            return false;
        }
        String lower = message.toLowerCase();
        return ADDRESS_NOT_FOUND_MARKERS.stream().anyMatch(lower::contains);
    }
}
