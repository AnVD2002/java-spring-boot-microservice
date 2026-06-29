package com.project.auth_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmRegistrationRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String otp;
}
