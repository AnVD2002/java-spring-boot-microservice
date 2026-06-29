package com.project.auth_service.service;

import com.project.auth_service.dto.request.*;
import com.project.auth_service.dto.response.AdminAccountResponse;
import com.project.auth_service.dto.response.PermissionResponse;
import com.project.auth_service.dto.response.RoleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface AdminAuthService {
    Page<AdminAccountResponse> getAccounts(String keyword, Integer status, Pageable pageable);

    AdminAccountResponse getAccount(UUID accountId);

    AdminAccountResponse updateAccountStatus(UUID accountId, AccountStatusUpdateRequest request);

    List<RoleResponse> getAccountRoles(UUID accountId);

    List<RoleResponse> updateAccountRoles(UUID accountId, AccountRolesUpdateRequest request);

    List<String> getAccountPermissions(UUID accountId);

    Page<RoleResponse> getRoles(String keyword, Pageable pageable);

    RoleResponse createRole(RoleRequest request);

    RoleResponse updateRole(Long roleId, RoleRequest request);

    void deleteRole(Long roleId);

    Page<PermissionResponse> getPermissions(String keyword, String module, String scope, Pageable pageable);

    PermissionResponse createPermission(PermissionRequest request);

    PermissionResponse updatePermission(Long permissionId, PermissionRequest request);

    void deletePermission(Long permissionId);

    List<PermissionResponse> getRolePermissions(Long roleId);

    List<PermissionResponse> updateRolePermissions(Long roleId, RolePermissionsUpdateRequest request);
}
