package com.project.common_lib_service.event;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetEmailEvent {
    private String email;
    private String otp;
}
