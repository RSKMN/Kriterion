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

    // Configuration: 5 attempts per minute
    private static final int MAX_ATTEMPTS = 5;
    private static final long TIME_WINDOW_MS = TimeUnit.MINUTES.toMillis(1);

    /**
     * Check if request is allowed for given IP.
     * Returns true if within rate limit, false if exceeded.
     */
    public boolean isAllowed(String clientIp) {
        RateLimitBucket bucket = buckets.compute(clientIp, (key, existing) -> {
            if (existing == null) {
                return new RateLimitBucket();
            }
            long elapsed = System.currentTimeMillis() - existing.resetTime;
            if (elapsed > TIME_WINDOW_MS) {
                // Reset bucket if time window expired
                existing.count.set(0);
                existing.resetTime = System.currentTimeMillis();
            }
            return existing;
        });

        int attempts = bucket.count.incrementAndGet();
        if (attempts > MAX_ATTEMPTS) {
            log.warn("Rate limit exceeded for IP: {} (attempts: {})", clientIp, attempts);
            return false;
        }
        return true;
    }

    /**
     * Get remaining attempts for IP.
     */
    public int getRemainingAttempts(String clientIp) {
        RateLimitBucket bucket = buckets.get(clientIp);
        if (bucket == null) {
            return MAX_ATTEMPTS;
        }
        long elapsed = System.currentTimeMillis() - bucket.resetTime;
        if (elapsed > TIME_WINDOW_MS) {
            return MAX_ATTEMPTS;
        }
        return Math.max(0, MAX_ATTEMPTS - bucket.count.get());
    }

    /**
     * Reset rate limit for IP (called on successful auth).
     */
    public void reset(String clientIp) {
        buckets.remove(clientIp);
    }

    /**
     * Periodic cleanup of expired buckets (call periodically).
     */
    public void cleanup() {
        long now = System.currentTimeMillis();
        buckets.entrySet().removeIf(entry -> 
            (now - entry.getValue().resetTime) > (TIME_WINDOW_MS * 2)
        );
    }
}
