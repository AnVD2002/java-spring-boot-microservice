package com.project.auth_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kafka.notification")
public class OtpTopicProperties {
    private String sendOtpEmail;
    private String sendConfirmationRegistrationEmail;
}
