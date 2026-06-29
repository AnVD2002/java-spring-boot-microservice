package com.project.user_service.cache;

import com.project.user_service.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserAdminCache {

    private static final Duration CACHE_TTL = Duration.ofMinutes(10);
    private static final String USER_CACHE_PREFIX = "user:admin:user:";
    private static final String USER_BY_ACCOUNT_CACHE_PREFIX = "user:admin:account:";

    private final RedisTemplate<String, Object> redisTemplate;

    public Optional<UserResponse> getUser(UUID userId) {
        Object cached = redisTemplate.opsForValue().get(USER_CACHE_PREFIX + userId);
        return cached instanceof UserResponse response ? Optional.of(response) : Optional.empty();
    }

    public void putUser(UUID userId, UserResponse response) {
        redisTemplate.opsForValue().set(USER_CACHE_PREFIX + userId, response, CACHE_TTL);
    }

    public Optional<UserResponse> getUserByAccountId(UUID accountId) {
        Object cached = redisTemplate.opsForValue().get(USER_BY_ACCOUNT_CACHE_PREFIX + accountId);
        return cached instanceof UserResponse response ? Optional.of(response) : Optional.empty();
    }

    public void putUserByAccountId(UUID accountId, UserResponse response) {
        redisTemplate.opsForValue().set(USER_BY_ACCOUNT_CACHE_PREFIX + accountId, response, CACHE_TTL);
    }

    public void evictUser(UUID userId, UUID accountId) {
        redisTemplate.delete(USER_CACHE_PREFIX + userId);
        redisTemplate.delete(USER_BY_ACCOUNT_CACHE_PREFIX + accountId);
    }
}
