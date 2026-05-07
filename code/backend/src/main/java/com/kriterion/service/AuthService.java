package com.kriterion.service;

import com.kriterion.dto.auth.RegisterRequest;
import com.kriterion.entity.User;
import com.kriterion.exception.ApiException;
import com.kriterion.repository.UserRepository;
import com.kriterion.security.ratelimit.RateLimiter;
import com.kriterion.security.util.PasswordSecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.kriterion.security.jwt.JwtTokenService jwtTokenService;
    private final com.kriterion.repository.RefreshTokenRepository refreshTokenRepository;
    private final com.kriterion.security.jwt.JwtProperties jwtProperties;
    private final RateLimiter rateLimiter;

    @Transactional
    public Long register(RegisterRequest request) {
        String email = request.getEmail().toLowerCase().trim();

        userRepository.findByEmail(email).ifPresent(u -> {
            log.warn("Duplicate registration attempt for email={}", email);
            throw new ApiException("Email already exists", HttpStatus.CONFLICT);
        });

        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEmailVerified(false);

        User saved = userRepository.save(user);
        log.info("User registered successfully: id={}, email={}", saved.getId(), saved.getEmail());
        return saved.getId();
    }

    @Transactional
    public com.kriterion.dto.auth.AuthResponse login(String email, String password) {
        String normalized = email.toLowerCase().trim();
        String clientIp = getClientIp();

        // Attempt to find user (don't expose non-existence to attacker)
        java.util.Optional<User> userOpt = userRepository.findByEmail(normalized);

        if (userOpt.isEmpty()) {
            // User doesn't exist - still perform dummy password check for timing resistance
            PasswordSecurityUtil.verifyPassword(password, "$2a$12$dummy", passwordEncoder);
            log.warn("Login attempt for non-existent email: {}", normalized);
            throw new ApiException(PasswordSecurityUtil.getGenericAuthError(), HttpStatus.UNAUTHORIZED);
        }

        User user = userOpt.get();

        // Verify password using timing-safe method
        if (!PasswordSecurityUtil.verifyPassword(password, user.getPasswordHash(), passwordEncoder)) {
            log.warn("Failed login attempt for email={}, ip={}", normalized, clientIp);
            throw new ApiException(PasswordSecurityUtil.getGenericAuthError(), HttpStatus.UNAUTHORIZED);
        }

        String subject = String.valueOf(user.getId());
        String accessToken = jwtTokenService.generateAccessToken(subject);
        String refreshToken = jwtTokenService.generateRefreshToken(subject);

        // persist refresh token with expiration based on configured properties
        com.kriterion.entity.RefreshToken rt = new com.kriterion.entity.RefreshToken();
        rt.setUserId(user.getId());
        rt.setToken(refreshToken);
        rt.setExpiresAt(java.time.LocalDateTime.now().plusDays(jwtProperties.getRefreshTokenExpirationDays()));
        rt.setRevoked(false);
        refreshTokenRepository.save(rt);

        // Reset rate limit on successful login
        rateLimiter.reset(clientIp);
        log.info("User logged in successfully: id={}, ip={}", user.getId(), clientIp);

        com.kriterion.dto.user.UserResponse userResp = com.kriterion.dto.user.UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();

        return new com.kriterion.dto.auth.AuthResponse(accessToken, refreshToken, userResp);
    }

    @Transactional
    public void revokeRefreshToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
            log.info("Refresh token revoked for userId={}", rt.getUserId());
        });
    }

    @Transactional
    public String refreshAccessToken(String refreshToken) {
        // validate presence
        com.kriterion.entity.RefreshToken stored = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ApiException("Invalid refresh token", org.springframework.http.HttpStatus.UNAUTHORIZED));

        if (Boolean.TRUE.equals(stored.getRevoked())) {
            log.warn("Attempt to use revoked refresh token id={}", stored.getId());
            throw new ApiException("Invalid refresh token", org.springframework.http.HttpStatus.UNAUTHORIZED);
        }

        if (stored.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            log.warn("Attempt to use expired refresh token id={}", stored.getId());
            throw new ApiException("Invalid refresh token", org.springframework.http.HttpStatus.UNAUTHORIZED);
        }

        // validate JWT signature/expiry
        if (!jwtTokenService.isTokenValid(refreshToken)) {
            throw new ApiException("Invalid refresh token", org.springframework.http.HttpStatus.UNAUTHORIZED);
        }

        String subject = jwtTokenService.extractSubject(refreshToken);
        // create new access token
        String newAccess = jwtTokenService.generateAccessToken(subject);
        log.info("Refresh token used for userId={}", subject);
        return newAccess;
    }

    /**
     * Extract client IP from HTTP request for rate limiting and logging.
     */
    private String getClientIp() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return "unknown";
        }

        String forwarded = attrs.getRequest().getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }

        String realIp = attrs.getRequest().getHeader("X-Real-IP");
        if (realIp != null && !realIp.isEmpty()) {
            return realIp;
        }

        return attrs.getRequest().getRemoteAddr();
    }
}
