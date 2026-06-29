package com.project.common_lib_service.repository;

import com.project.common_lib_service.dto.RefreshTokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PREFIX = "refresh:";
    private static final String ACCOUNT_INDEX_PREFIX = "refresh:account:";

    public void save(RefreshTokenInfo tokenInfo) {
        String key = PREFIX + tokenInfo.getJti();
        String accountIndexKey = ACCOUNT_INDEX_PREFIX + tokenInfo.getAccountId();
        long ttl = tokenInfo.getExpiryTime() - System.currentTimeMillis();
        redisTemplate.opsForValue().set(key, tokenInfo, ttl, TimeUnit.MILLISECONDS);
        redisTemplate.opsForSet().add(accountIndexKey, tokenInfo.getJti());
        redisTemplate.expire(accountIndexKey, ttl, TimeUnit.MILLISECONDS);
    }

    public Optional<RefreshTokenInfo> findByJti(String jti) {
        Object value = redisTemplate.opsForValue().get(PREFIX + jti);
        return Optional.ofNullable((RefreshTokenInfo) value);
    }

    public void delete(String jti) {
        findByJti(jti).ifPresent(tokenInfo ->
                redisTemplate.opsForSet().remove(ACCOUNT_INDEX_PREFIX + tokenInfo.getAccountId(), jti));
        redisTemplate.delete(PREFIX + jti);
    }

    public void deleteAllByAccountId(UUID accountId) {
        String accountIndexKey = ACCOUNT_INDEX_PREFIX + accountId;
        Set<Object> jtis = redisTemplate.opsForSet().members(accountIndexKey);
        if (jtis != null) {
            jtis.forEach(jti -> redisTemplate.delete(PREFIX + jti));
        }
        redisTemplate.delete(accountIndexKey);
    }

}
