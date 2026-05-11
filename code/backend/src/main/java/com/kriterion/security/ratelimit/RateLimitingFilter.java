package com.kriterion.security.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kriterion.dto.shared.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Rate limiting filter for authentication endpoints.
 * Protects against brute-force attacks by limiting requests per IP.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimiter rateLimiter;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String uri = request.getRequestURI();
        String clientIp = getClientIp(request);
        boolean allowed = true;

        if (uri.contains("/auth/login") || uri.contains("/auth/register")) {
            allowed = rateLimiter.isAllowed(clientIp + ":auth", 5, java.util.concurrent.TimeUnit.MINUTES.toMillis(1));
        } else if (uri.contains("/auth/refresh-token")) {
            allowed = rateLimiter.isAllowed(clientIp + ":refresh", 10, java.util.concurrent.TimeUnit.MINUTES.toMillis(1));
        } else if (uri.contains("/ocr/upload")) {
            allowed = rateLimiter.isAllowed(clientIp + ":ocr", 3, java.util.concurrent.TimeUnit.MINUTES.toMillis(1));
        } else if (uri.startsWith("/api/")) {
            allowed = rateLimiter.isAllowed(clientIp + ":api", 100, java.util.concurrent.TimeUnit.MINUTES.toMillis(1));
        }

        if (!allowed) {
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ApiResponse<Object> errorResponse = ApiResponse.<Object>builder()
                    .success(false)
                    .message("Too many requests. Please try again later.")
                    .build();
            objectMapper.writeValue(response.getWriter(), errorResponse);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        // Check X-Forwarded-For header first (for proxied requests)
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        // Check X-Real-IP header
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isEmpty()) {
            return realIp;
        }
        // Fall back to remote address
        return request.getRemoteAddr();
    }
}
