package com.project.notification_service.service.impl;

import com.project.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

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
        mailSender.send(message);
    }
}
