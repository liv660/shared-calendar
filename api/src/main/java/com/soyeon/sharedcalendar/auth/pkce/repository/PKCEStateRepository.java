package com.soyeon.sharedcalendar.auth.pkce.repository;

import com.soyeon.sharedcalendar.auth.pkce.domain.PKCELoginContext;
import io.micrometer.common.lang.Nullable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class PKCEStateRepository {
    private final RedisTemplate<String, Object> redis;
    private static final String REDIS_KEY_PREFIX = "oauth:state:";

    public PKCEStateRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redis = redisTemplate;
    }

    public void save(String state, PKCELoginContext context, Duration ttl) {
        redis.opsForValue().set(REDIS_KEY_PREFIX + state, context, ttl);
    }

    public @Nullable PKCELoginContext find(String state) {
        Object v = redis.opsForValue().get(REDIS_KEY_PREFIX + state);
        return (v instanceof PKCELoginContext context) ? context : null;
    }

    public void delete(String state) {
        redis.delete(REDIS_KEY_PREFIX + state);
    }
}
