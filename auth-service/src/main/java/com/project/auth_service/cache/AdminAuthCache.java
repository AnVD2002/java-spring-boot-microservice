package com.project.auth_service.cache;

import com.project.auth_service.dto.response.RoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AdminAuthCache {

    private static final Duration CACHE_TTL = Duration.ofMinutes(10);
    private static final String ROLES_CACHE_KEY = "auth:admin:roles";
    private static final String PERMISSIONS_CACHE_KEY = "auth:admin:permissions";
    private static final String ACCOUNT_PERMISSIONS_CACHE_PREFIX = "auth:admin:account-permissions:";

    private final RedisTemplate<String, Object> redisTemplate;

    public Optional<List<RoleResponse>> getRoles() {
        Object cached = redisTemplate.opsForValue().get(ROLES_CACHE_KEY);
        if (!(cached instanceof List<?> cachedList)) {
            return Optional.empty();
        }
        return Optional.of(cachedList.stream()
                .filter(RoleResponse.class::isInstance)
                .map(RoleResponse.class::cast)
                .toList());
    }

    public void putRoles(List<RoleResponse> roles) {
        redisTemplate.opsForValue().set(ROLES_CACHE_KEY, roles, CACHE_TTL);
    }

    public Optional<List<String>> getAccountPermissions(UUID accountId) {
        Object cached = redisTemplate.opsForValue().get(accountPermissionsKey(accountId));
        if (!(cached instanceof List<?> cachedList)) {
            return Optional.empty();
        }
        return Optional.of(cachedList.stream().map(String::valueOf).toList());
    }

    public void putAccountPermissions(UUID accountId, List<String> permissions) {
        redisTemplate.opsForValue().set(accountPermissionsKey(accountId), permissions, CACHE_TTL);
    }

    public void evictRoleCaches() {
        redisTemplate.delete(ROLES_CACHE_KEY);
        evictPermissionCaches();
    }

    public void evictPermissionCaches() {
        redisTemplate.delete(PERMISSIONS_CACHE_KEY);
    }

    public void evictAccountPermissions(UUID accountId) {
        redisTemplate.delete(accountPermissionsKey(accountId));
    }

    private String accountPermissionsKey(UUID accountId) {
        return ACCOUNT_PERMISSIONS_CACHE_PREFIX + accountId;
    }
}
