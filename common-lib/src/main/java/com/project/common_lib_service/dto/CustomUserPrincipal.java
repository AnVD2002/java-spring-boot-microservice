package com.project.common_lib_service.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CustomUserPrincipal {
    private UUID userId;
    private String username;
    private List<String> roles;
}
