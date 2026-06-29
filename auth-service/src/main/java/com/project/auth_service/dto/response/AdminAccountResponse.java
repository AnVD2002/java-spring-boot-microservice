package com.project.auth_service.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class AdminAccountResponse {
    private UUID id;
    private String email;
    private String username;
    private Integer status;
    private List<RoleResponse> roles;
    private List<String> permissions;
    private Instant insertedAt;
    private Instant updatedAt;
}
