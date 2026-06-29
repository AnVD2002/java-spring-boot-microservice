package com.project.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionRequest {
    @NotBlank
    private String code;

    @NotBlank
    private String name;

    @NotBlank
    private String module;

    @NotBlank
    private String scope;

    private String description;
}
