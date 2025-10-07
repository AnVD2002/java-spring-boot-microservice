package com.project.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountRegistrationRequest {

    @NotBlank
    private String token;

    @NotBlank(message = "Username must not be blank")
    @Size(min = 6, max = 20, message = "Username must be between 6 and 20 characters")
    private String username;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "Confirm password must not be blank")
    @Size(min = 8, message = "Confirm password must be at least 8 characters long")
    private String confirmPassword;
}

