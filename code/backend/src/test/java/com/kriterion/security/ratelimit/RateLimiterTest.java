package com.kriterion.security.ratelimit;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for rate limiting functionality.
 */
public class RateLimiterTest {

    private RateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        rateLimiter = new RateLimiter();
    }

    @Test
    void allowsRequestsWithinLimit() {
        String ip = "192.168.1.1";
        // First 5 requests should be allowed
        for (int i = 0; i < 5; i++) {
            assertTrue(rateLimiter.isAllowed(ip));
        }
    }

    @Test
    void blocksRequestsExceedingLimit() {
        String ip = "192.168.1.2";
        // First 5 requests allowed
        for (int i = 0; i < 5; i++) {
            assertTrue(rateLimiter.isAllowed(ip));
        }
        // 6th request should be blocked
        assertFalse(rateLimiter.isAllowed(ip));
    }

    @Test
    void resetsLimitOnSuccess() {
        String ip = "192.168.1.3";
        // Exhaust limit
        for (int i = 0; i < 5; i++) {
            rateLimiter.isAllowed(ip);
        }
        assertFalse(rateLimiter.isAllowed(ip));

        // Reset should allow subsequent requests
        rateLimiter.reset(ip);
        assertTrue(rateLimiter.isAllowed(ip));
    }

    @Test
    void tracksMultipleIpsIndependently() {
        String ip1 = "192.168.1.4";
        String ip2 = "192.168.1.5";

        // Exhaust limit for ip1
        for (int i = 0; i < 5; i++) {
            rateLimiter.isAllowed(ip1);
        }
        assertFalse(rateLimiter.isAllowed(ip1));

        // ip2 should still have allowance
        assertTrue(rateLimiter.isAllowed(ip2));
    }

    @Test
    void calculatesRemainingAttemptsCorrectly() {
        String ip = "192.168.1.6";
        assertEquals(5, rateLimiter.getRemainingAttempts(ip));
        
        rateLimiter.isAllowed(ip);
        assertEquals(4, rateLimiter.getRemainingAttempts(ip));

        rateLimiter.isAllowed(ip);
        assertEquals(3, rateLimiter.getRemainingAttempts(ip));
    }
}
