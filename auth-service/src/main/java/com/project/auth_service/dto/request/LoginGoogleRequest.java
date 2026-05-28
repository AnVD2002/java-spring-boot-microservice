package com.project.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginGoogleRequest {
    @NotBlank(message = "Token must not be blank")
    String token;
}
