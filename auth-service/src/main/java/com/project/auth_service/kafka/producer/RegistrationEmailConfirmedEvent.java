package com.project.auth_service.kafka.producer;

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
