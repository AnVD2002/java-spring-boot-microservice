package com.project.auth_service.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerifyOtpResponse {
    private String resetToken;
}
