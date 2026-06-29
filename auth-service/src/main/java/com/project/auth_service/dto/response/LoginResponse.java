package com.project.auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    String accessToken;
    String refreshToken;
    UUID deviceId;
    String username;
    String email;
    List<String> roles;
}
