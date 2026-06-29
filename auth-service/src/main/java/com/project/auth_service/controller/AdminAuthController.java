package com.project.auth_service.controller;

import com.project.auth_service.dto.request.*;
import com.project.auth_service.dto.response.AdminAccountResponse;
import com.project.auth_service.dto.response.PermissionResponse;
import com.project.auth_service.dto.response.RoleResponse;
import com.project.auth_service.service.AdminAuthService;
import com.project.common_lib_service.dto.ResponseData;
import com.project.common_lib_service.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER','SUPPORT','SUPER_ADMIN')")
@Tag(name = "Auth Admin", description = "Account, role and permission management APIs")
@SecurityRequirement(name = "bearerAuth")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @GetMapping("/accounts")
    @Operation(summary = "Search accounts", description = "Search and filter accounts for the user management dashboard")
    public ResponseEntity<ResponseData<Page<AdminAccountResponse>>> getAccounts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @PageableDefault(size = 20, sort = "insertedAt") Pageable pageable) {
        return ResponseUtils.success(adminAuthService.getAccounts(keyword, status, pageable));
    }

    @GetMapping("/accounts/{accountId}")
    @Operation(summary = "Get account detail")
    public ResponseEntity<ResponseData<AdminAccountResponse>> getAccount(@PathVariable UUID accountId) {
        return ResponseUtils.success(adminAuthService.getAccount(accountId));
    }

    @PatchMapping("/accounts/{accountId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update account status", description = "Lock, unlock, suspend or reactivate an auth account")
    public ResponseEntity<ResponseData<AdminAccountResponse>> updateAccountStatus(
            @PathVariable UUID accountId,
            @Valid @RequestBody AccountStatusUpdateRequest request) {
        return ResponseUtils.success(adminAuthService.updateAccountStatus(accountId, request));
    }

    @GetMapping("/accounts/{accountId}/roles")
    @Operation(summary = "Get account roles")
    public ResponseEntity<ResponseData<List<RoleResponse>>> getAccountRoles(@PathVariable UUID accountId) {
        return ResponseUtils.success(adminAuthService.getAccountRoles(accountId));
    }

    @PutMapping("/accounts/{accountId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Replace account roles")
    public ResponseEntity<ResponseData<List<RoleResponse>>> updateAccountRoles(
            @PathVariable UUID accountId,
            @Valid @RequestBody AccountRolesUpdateRequest request) {
        return ResponseUtils.success(adminAuthService.updateAccountRoles(accountId, request));
    }

    @GetMapping("/accounts/{accountId}/permissions")
    @Operation(summary = "Get account effective permissions")
    public ResponseEntity<ResponseData<List<String>>> getAccountPermissions(@PathVariable UUID accountId) {
        return ResponseUtils.success(adminAuthService.getAccountPermissions(accountId));
    }

    @GetMapping("/roles")
    @Operation(summary = "Search roles")
    public ResponseEntity<ResponseData<Page<RoleResponse>>> getRoles(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 50, sort = "name") Pageable pageable) {
        return ResponseUtils.success(adminAuthService.getRoles(keyword, pageable));
    }

    @PostMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create role")
    public ResponseEntity<ResponseData<RoleResponse>> createRole(@Valid @RequestBody RoleRequest request) {
        return ResponseUtils.success(adminAuthService.createRole(request));
    }

    @PutMapping("/roles/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update role")
    public ResponseEntity<ResponseData<RoleResponse>> updateRole(
            @PathVariable Long roleId,
            @Valid @RequestBody RoleRequest request) {
        return ResponseUtils.success(adminAuthService.updateRole(roleId, request));
    }

    @DeleteMapping("/roles/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete role")
    public ResponseEntity<ResponseData<Void>> deleteRole(@PathVariable Long roleId) {
        adminAuthService.deleteRole(roleId);
        return ResponseUtils.success();
    }

    @GetMapping("/permissions")
    @Operation(summary = "Search permissions")
    public ResponseEntity<ResponseData<Page<PermissionResponse>>> getPermissions(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String scope,
            @PageableDefault(size = 50, sort = "code") Pageable pageable) {
        return ResponseUtils.success(adminAuthService.getPermissions(keyword, module, scope, pageable));
    }

    @PostMapping("/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create permission")
    public ResponseEntity<ResponseData<PermissionResponse>> createPermission(@Valid @RequestBody PermissionRequest request) {
        return ResponseUtils.success(adminAuthService.createPermission(request));
    }

    @PutMapping("/permissions/{permissionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update permission")
    public ResponseEntity<ResponseData<PermissionResponse>> updatePermission(
            @PathVariable Long permissionId,
            @Valid @RequestBody PermissionRequest request) {
        return ResponseUtils.success(adminAuthService.updatePermission(permissionId, request));
    }

    @DeleteMapping("/permissions/{permissionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete permission")
    public ResponseEntity<ResponseData<Void>> deletePermission(@PathVariable Long permissionId) {
        adminAuthService.deletePermission(permissionId);
        return ResponseUtils.success();
    }

    @GetMapping("/roles/{roleId}/permissions")
    @Operation(summary = "Get role permissions")
    public ResponseEntity<ResponseData<List<PermissionResponse>>> getRolePermissions(@PathVariable Long roleId) {
        return ResponseUtils.success(adminAuthService.getRolePermissions(roleId));
    }

    @PutMapping("/roles/{roleId}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Replace role permissions")
    public ResponseEntity<ResponseData<List<PermissionResponse>>> updateRolePermissions(
            @PathVariable Long roleId,
            @Valid @RequestBody RolePermissionsUpdateRequest request) {
        return ResponseUtils.success(adminAuthService.updateRolePermissions(roleId, request));
    }
}
