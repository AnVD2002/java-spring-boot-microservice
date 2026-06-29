package com.project.auth_service.service.impl;

import com.project.auth_service.dto.request.*;
import com.project.auth_service.dto.response.AdminAccountResponse;
import com.project.auth_service.dto.response.PermissionResponse;
import com.project.auth_service.dto.response.RoleResponse;
import com.project.auth_service.entity.*;
import com.project.auth_service.cache.AdminAuthCache;
import com.project.auth_service.mapper.AdminAuthMapper;
import com.project.auth_service.repository.*;
import com.project.auth_service.service.AdminAuthService;
import com.project.common_lib_service.service.AuditLogService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.project.auth_service.enums.AuditAction.CREATE;
import static com.project.auth_service.enums.AuditAction.DELETE;
import static com.project.auth_service.enums.AuditAction.UPDATE;
import static com.project.auth_service.enums.AuditEntityType.ACCOUNT;
import static com.project.auth_service.enums.AuditEntityType.ACCOUNT_ROLES;
import static com.project.auth_service.enums.AuditEntityType.PERMISSION;
import static com.project.auth_service.enums.AuditEntityType.ROLE;
import static com.project.auth_service.enums.AuditEntityType.ROLE_PERMISSIONS;

@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final AdminAuthMapper mapper;
    private final AdminAuthCache cache;
    private final AuditLogService auditLogService;

    @Override
    public Page<AdminAccountResponse> getAccounts(String keyword, Integer status, Pageable pageable) {
        return accountRepository.searchForAdmin(normalizeLikeKeyword(keyword), status, pageable).map(this::toAccountResponse);
    }

    @Override
    public AdminAccountResponse getAccount(UUID accountId) {
        return toAccountResponse(getAccountOrThrow(accountId));
    }

    @Override
    @Transactional
    public AdminAccountResponse updateAccountStatus(UUID accountId, AccountStatusUpdateRequest request) {
        Account account = getAccountOrThrow(accountId);
        Map<String, Object> oldData = accountSnapshot(account);
        account.setStatus(request.getStatus());
        Account saved = accountRepository.save(account);
        auditLogService.record(UPDATE, ACCOUNT, accountId, oldData, accountSnapshot(saved));
        cache.evictAccountPermissions(accountId);
        return toAccountResponse(saved);
    }

    @Override
    public List<RoleResponse> getAccountRoles(UUID accountId) {
        getAccountOrThrow(accountId);
        return accountRoleRepository.findRolesByAccountId(accountId).stream()
                .map(mapper::toRoleResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<RoleResponse> updateAccountRoles(UUID accountId, AccountRolesUpdateRequest request) {
        getAccountOrThrow(accountId);
        List<Long> oldRoleIds = accountRoleRepository.findByAccountId(accountId).stream()
                .map(AccountRole::getRoleId)
                .toList();
        List<Role> roles = roleRepository.findAllById(request.getRoleIds());
        if (roles.size() != new HashSet<>(request.getRoleIds()).size()) {
            throw new NoSuchElementException("One or more roles were not found");
        }

        accountRoleRepository.deleteByAccountId(accountId);
        roles.forEach(role -> accountRoleRepository.save(AccountRole.builder()
                .accountId(accountId)
                .roleId(role.getId())
                .build()));
        auditLogService.record(
                UPDATE,
                ACCOUNT_ROLES,
                accountId,
                Map.of("roleIds", oldRoleIds),
                Map.of("roleIds", roles.stream().map(Role::getId).toList())
        );
        cache.evictAccountPermissions(accountId);

        return roles.stream().map(mapper::toRoleResponse).toList();
    }

    @Override
    public List<String> getAccountPermissions(UUID accountId) {
        getAccountOrThrow(accountId);
        Optional<List<String>> cached = cache.getAccountPermissions(accountId);
        if (cached.isPresent()) {
            return cached.get();
        }

        List<Long> roleIds = accountRoleRepository.findByAccountId(accountId).stream()
                .map(AccountRole::getRoleId)
                .toList();
        List<String> permissions = roleIds.isEmpty()
                ? List.of()
                : rolePermissionRepository.findPermissionCodesByRoleIds(roleIds);
        cache.putAccountPermissions(accountId, permissions);
        return permissions;
    }

    @Override
    public Page<RoleResponse> getRoles(String keyword, Pageable pageable) {
        if (!StringUtils.hasText(keyword) && pageable.isUnpaged()) {
            Optional<List<RoleResponse>> cached = cache.getRoles();
            if (cached.isPresent()) {
                return new org.springframework.data.domain.PageImpl<>(cached.get());
            }
        }

        Page<RoleResponse> result = roleRepository.searchForAdmin(normalize(keyword), pageable).map(mapper::toRoleResponse);
        if (!StringUtils.hasText(keyword) && pageable.isUnpaged()) {
            cache.putRoles(result.getContent());
        }
        return result;
    }

    @Override
    @Transactional
    public RoleResponse createRole(RoleRequest request) {
        Role role = roleRepository.save(Role.builder().name(request.getName()).build());
        auditLogService.record(CREATE, ROLE, role.getId(), null, roleSnapshot(role));
        cache.evictRoleCaches();
        return mapper.toRoleResponse(role);
    }

    @Override
    @Transactional
    public RoleResponse updateRole(Long roleId, RoleRequest request) {
        Role role = getRoleOrThrow(roleId);
        Map<String, Object> oldData = roleSnapshot(role);
        role.setName(request.getName());
        Role saved = roleRepository.save(role);
        auditLogService.record(UPDATE, ROLE, roleId, oldData, roleSnapshot(saved));
        cache.evictRoleCaches();
        return mapper.toRoleResponse(saved);
    }

    @Override
    @Transactional
    public void deleteRole(Long roleId) {
        Role role = getRoleOrThrow(roleId);
        Map<String, Object> oldData = roleSnapshot(role);
        rolePermissionRepository.deleteByRoleId(roleId);
        roleRepository.deleteById(roleId);
        auditLogService.record(DELETE, ROLE, roleId, oldData, null);
        cache.evictRoleCaches();
    }

    @Override
    public Page<PermissionResponse> getPermissions(String keyword, String module, String scope, Pageable pageable) {
        Page<PermissionResponse> result = permissionRepository.searchForAdmin(
                        normalize(keyword),
                        normalize(module),
                        normalize(scope),
                        pageable
                )
                .map(mapper::toPermissionResponse);
        if (!StringUtils.hasText(keyword) && !StringUtils.hasText(module) && !StringUtils.hasText(scope) && pageable.isUnpaged()) {
            cache.evictPermissionCaches();
        }
        return result;
    }

    @Override
    @Transactional
    public PermissionResponse createPermission(PermissionRequest request) {
        Permission permission = Permission.builder()
                .code(request.getCode())
                .name(request.getName())
                .module(request.getModule())
                .scope(request.getScope())
                .description(request.getDescription())
                .build();
        Permission saved = permissionRepository.save(permission);
        auditLogService.record(CREATE, PERMISSION, saved.getId(), null, permissionSnapshot(saved));
        cache.evictPermissionCaches();
        return mapper.toPermissionResponse(saved);
    }

    @Override
    @Transactional
    public PermissionResponse updatePermission(Long permissionId, PermissionRequest request) {
        Permission permission = getPermissionOrThrow(permissionId);
        Map<String, Object> oldData = permissionSnapshot(permission);
        permission.setCode(request.getCode());
        permission.setName(request.getName());
        permission.setModule(request.getModule());
        permission.setScope(request.getScope());
        permission.setDescription(request.getDescription());
        Permission saved = permissionRepository.save(permission);
        auditLogService.record(UPDATE, PERMISSION, permissionId, oldData, permissionSnapshot(saved));
        cache.evictPermissionCaches();
        return mapper.toPermissionResponse(saved);
    }

    @Override
    @Transactional
    public void deletePermission(Long permissionId) {
        Permission permission = getPermissionOrThrow(permissionId);
        permissionRepository.deleteById(permissionId);
        auditLogService.record(DELETE, PERMISSION, permissionId, permissionSnapshot(permission), null);
        cache.evictPermissionCaches();
    }

    @Override
    public List<PermissionResponse> getRolePermissions(Long roleId) {
        getRoleOrThrow(roleId);
        return rolePermissionRepository.findPermissionsByRoleId(roleId).stream()
                .map(mapper::toPermissionResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<PermissionResponse> updateRolePermissions(Long roleId, RolePermissionsUpdateRequest request) {
        getRoleOrThrow(roleId);
        List<Long> oldPermissionIds = rolePermissionRepository.findByRoleId(roleId).stream()
                .map(RolePermission::getPermissionId)
                .toList();
        List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());
        if (permissions.size() != new HashSet<>(request.getPermissionIds()).size()) {
            throw new NoSuchElementException("One or more permissions were not found");
        }

        rolePermissionRepository.deleteByRoleId(roleId);
        permissions.forEach(permission -> rolePermissionRepository.save(RolePermission.builder()
                .roleId(roleId)
                .permissionId(permission.getId())
                .build()));
        auditLogService.record(
                UPDATE,
                ROLE_PERMISSIONS,
                roleId,
                Map.of("permissionIds", oldPermissionIds),
                Map.of("permissionIds", permissions.stream().map(Permission::getId).toList())
        );
        cache.evictPermissionCaches();
        return permissions.stream().map(mapper::toPermissionResponse).toList();
    }

    private AdminAccountResponse toAccountResponse(Account account) {
        List<RoleResponse> roles = getAccountRoles(account.getId());
        return mapper.toAccountResponse(account, roles, getAccountPermissions(account.getId()));
    }

    private Account getAccountOrThrow(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new NoSuchElementException("Account not found: " + accountId));
    }

    private Role getRoleOrThrow(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new NoSuchElementException("Role not found: " + roleId));
    }

    private Permission getPermissionOrThrow(Long permissionId) {
        return permissionRepository.findById(permissionId)
                .orElseThrow(() -> new NoSuchElementException("Permission not found: " + permissionId));
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String normalizeLikeKeyword(String value) {
        return StringUtils.hasText(value) ? value.trim().toLowerCase(Locale.ROOT) : "";
    }

    private Map<String, Object> accountSnapshot(Account account) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", account.getId());
        data.put("email", account.getEmail());
        data.put("username", account.getUsername());
        data.put("status", account.getStatus());
        return data;
    }

    private Map<String, Object> roleSnapshot(Role role) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", role.getId());
        data.put("name", role.getName());
        return data;
    }

    private Map<String, Object> permissionSnapshot(Permission permission) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", permission.getId());
        data.put("code", permission.getCode());
        data.put("name", permission.getName());
        data.put("module", permission.getModule());
        data.put("scope", permission.getScope());
        data.put("description", permission.getDescription());
        return data;
    }

}
