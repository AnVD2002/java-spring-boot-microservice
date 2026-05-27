package com.project.notification_service.kafka.consumer;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationEmailConfirmedEvent {
    private String email;
    private String otp;
}
