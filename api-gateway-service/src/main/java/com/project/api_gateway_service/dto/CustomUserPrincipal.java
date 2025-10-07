package com.project.api_gateway_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class CustomUserPrincipal {
    private UUID userId;
    private String username;
    private String role;
}
