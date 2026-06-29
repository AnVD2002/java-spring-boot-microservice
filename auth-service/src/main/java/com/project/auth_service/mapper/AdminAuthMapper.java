package com.project.auth_service.mapper;

import com.project.auth_service.dto.response.AdminAccountResponse;
import com.project.auth_service.dto.response.PermissionResponse;
import com.project.auth_service.dto.response.RoleResponse;
import com.project.auth_service.entity.Account;
import com.project.auth_service.entity.Permission;
import com.project.auth_service.entity.Role;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminAuthMapper {

    public AdminAccountResponse toAccountResponse(Account account, List<RoleResponse> roles, List<String> permissions) {
        return AdminAccountResponse.builder()
                .id(account.getId())
                .email(account.getEmail())
                .username(account.getUsername())
                .status(account.getStatus())
                .roles(roles)
                .permissions(permissions)
                .insertedAt(account.getInsertedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

    public RoleResponse toRoleResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }

    public PermissionResponse toPermissionResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .code(permission.getCode())
                .name(permission.getName())
                .module(permission.getModule())
                .scope(permission.getScope())
                .description(permission.getDescription())
                .build();
    }
}
