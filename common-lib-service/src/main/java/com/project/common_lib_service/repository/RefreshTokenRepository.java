package com.project.common_lib_service.repository;

import com.project.common_lib_service.dto.RefreshTokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class RefreshTokenRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PREFIX = "refresh:";

    public void save(RefreshTokenInfo tokenInfo) {
        String key = PREFIX + tokenInfo.getJti();
        long ttl = tokenInfo.getExpiryTime() - System.currentTimeMillis();
        redisTemplate.opsForValue().set(key, tokenInfo, ttl, TimeUnit.MILLISECONDS);
    }

    public Optional<RefreshTokenInfo> findByJti(String jti) {
        Object value = redisTemplate.opsForValue().get(PREFIX + jti);
        return Optional.ofNullable((RefreshTokenInfo) value);
    }

    public void delete(String jti) {
        redisTemplate.delete(PREFIX + jti);
    }

}
