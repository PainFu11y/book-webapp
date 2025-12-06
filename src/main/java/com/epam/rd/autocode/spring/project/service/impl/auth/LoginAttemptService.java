package com.epam.rd.autocode.spring.project.service.impl.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final int MAX_ATTEMPTS = 5;
    private static final int BLOCK_TIME_MINUTES = 15;

    private String attemptsKey(String email) {
        return "failed:" + email;
    }

    private String blockKey(String email) {
        return "blocked:" + email;
    }

    public void increaseFailedAttempts(String email) {
        String key = attemptsKey(email);
        Long attempts = redisTemplate.opsForValue().increment(key);

        if (attempts != null && attempts >= MAX_ATTEMPTS) {
            redisTemplate.opsForValue().set(
                    blockKey(email),
                    "LOCKED",
                    Duration.ofMinutes(BLOCK_TIME_MINUTES)
            );
            log.info("User {} is blocked for {} minutes", email, BLOCK_TIME_MINUTES);
        } else {
            log.info("Failed attempts for {}: {}", email, attempts);
        }
    }

    public void resetAttempts(String email) {
        redisTemplate.delete(attemptsKey(email));
        redisTemplate.delete(blockKey(email));
        log.info("Failed attempts reset for {}", email);
    }

    public boolean isBlocked(String email) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(blockKey(email)));
    }

    public long getRemainingBlockTime(String email) {
        Long expire = redisTemplate.getExpire(blockKey(email));
        return expire != null ? expire : 0;
    }
}
