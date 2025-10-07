package com.project.common_lib_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenInfo {
    private String jti;
    private String username;
    private UUID accountId;
    private List<String> roles;
    private long expiryTime;
}
