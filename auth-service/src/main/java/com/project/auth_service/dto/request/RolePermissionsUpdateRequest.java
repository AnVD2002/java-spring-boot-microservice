package com.project.auth_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RolePermissionsUpdateRequest {
    @NotNull
    private List<Long> permissionIds;
}
