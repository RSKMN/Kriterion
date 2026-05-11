package com.kriterion.security.ratelimit;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * In-memory rate limiter for authentication endpoints.
 * Tracks requests per IP address with configurable limits and time windows.
 */
@Component
@Slf4j
public class RateLimiter {

    private static class RateLimitBucket {
        AtomicInteger count = new AtomicInteger(0);
        long resetTime = System.currentTimeMillis();
    }

    private final ConcurrentHashMap<String, RateLimitBucket> buckets = new ConcurrentHashMap<>();

    // Default global limits
    private static final int DEFAULT_MAX_ATTEMPTS = 60;
    private static final long DEFAULT_WINDOW_MS = TimeUnit.MINUTES.toMillis(1);

    /**
     * Check if request is allowed for given key (usually IP + endpoint).
     */
    public boolean isAllowed(String key, int maxAttempts, long windowMs) {
        RateLimitBucket bucket = buckets.compute(key, (k, existing) -> {
            if (existing == null) {
                return new RateLimitBucket();
            }
            long elapsed = System.currentTimeMillis() - existing.resetTime;
            if (elapsed > windowMs) {
                existing.count.set(0);
                existing.resetTime = System.currentTimeMillis();
            }
            return existing;
        });

        int attempts = bucket.count.incrementAndGet();
        if (attempts > maxAttempts) {
            log.warn("Rate limit exceeded for key: {} (attempts: {})", key, attempts);
            return false;
        }
        return true;
    }

    public boolean isAllowed(String clientIp) {
        return isAllowed(clientIp, DEFAULT_MAX_ATTEMPTS, DEFAULT_WINDOW_MS);
    }

    public void reset(String key) {
        buckets.remove(key);
    }

    public int getRemainingAttempts(String key) {
        RateLimitBucket bucket = buckets.get(key);
        if (bucket == null) return DEFAULT_MAX_ATTEMPTS;
        return Math.max(0, DEFAULT_MAX_ATTEMPTS - bucket.count.get());
    }
}
