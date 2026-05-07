package com.kriterion.security.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kriterion.response.ApiResponse;
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

    private static final String[] PROTECTED_PATHS = {
            "/auth/login",
            "/auth/register",
            "/auth/refresh-token"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Only apply rate limiting to specific auth endpoints
        if (isProtectedPath(request.getRequestURI())) {
            String clientIp = getClientIp(request);

            if (!rateLimiter.isAllowed(clientIp)) {
                // Rate limit exceeded
                response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                ApiResponse<Object> errorResponse = ApiResponse.<Object>builder()
                        .success(false)
                        .message("Too many requests. Please try again later.")
                        .data(null)
                        .build();

                objectMapper.writeValue(response.getWriter(), errorResponse);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isProtectedPath(String requestUri) {
        for (String path : PROTECTED_PATHS) {
            // Match path with or without context prefix
            if (requestUri.endsWith(path) || requestUri.contains(path)) {
                return true;
            }
        }
        return false;
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
