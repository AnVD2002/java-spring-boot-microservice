package com.project.notification_service.service;

public interface EmailService {
    void sendOtpEmail(String to, String otp);
}
