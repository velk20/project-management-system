package com.mladenov.projectmanagement.auth.service;

import java.time.Instant;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenInvalidationService {
    private static final String GLOBAL_INVALID_BEFORE_KEY = "global:jwt:invalidBefore";

    private final RedisTemplate<String, String> redisTemplate;

    public TokenInvalidationService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Sets the value of the {@link #GLOBAL_INVALID_BEFORE_KEY}
     * to the current no
     */
    public void invalidateAllTokens() {
        String now = String.valueOf(Instant.now().toEpochMilli());
        redisTemplate.opsForValue().set(GLOBAL_INVALID_BEFORE_KEY, now);
    }


    /**
     * Checks if the token-issued date
     * is before the already set value
     * for invalid tokens
     * @param tokenIssuedAt - JWT token issued date
     * @return if the token date is before the value set for invalidation
     */
    public boolean isTokenInvalid(Instant tokenIssuedAt) {
        String invalidBeforeStr = redisTemplate.opsForValue().get(GLOBAL_INVALID_BEFORE_KEY);
        if (invalidBeforeStr == null) {
            return false;
        }
        Instant invalidBefore = Instant.ofEpochMilli(Long.parseLong(invalidBeforeStr));
        return tokenIssuedAt.isBefore(invalidBefore);
    }
}
