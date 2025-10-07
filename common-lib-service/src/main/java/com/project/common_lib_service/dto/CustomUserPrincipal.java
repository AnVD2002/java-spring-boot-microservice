package com.project.common_lib_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class CustomUserPrincipal {
    private UUID userId;
    private String username;
    private List<String> roles;
}
