package com.project.auth_service.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PermissionResponse {
    private Long id;
    private String code;
    private String name;
    private String module;
    private String scope;
    private String description;
}
