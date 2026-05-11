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
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.kriterion.security.jwt.JwtTokenService jwtTokenService;
    private final com.kriterion.repository.RefreshTokenRepository refreshTokenRepository;
    private final com.kriterion.security.jwt.JwtProperties jwtProperties;
    private final RateLimiter rateLimiter;
    private final com.kriterion.service.mobile.MobileService mobileService;
    private final com.kriterion.service.SessionService sessionService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       com.kriterion.security.jwt.JwtTokenService jwtTokenService,
                       com.kriterion.repository.RefreshTokenRepository refreshTokenRepository,
                       com.kriterion.security.jwt.JwtProperties jwtProperties,
                       RateLimiter rateLimiter,
                       com.kriterion.service.mobile.MobileService mobileService,
                       com.kriterion.service.SessionService sessionService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
        this.rateLimiter = rateLimiter;
        this.mobileService = mobileService;
        this.sessionService = sessionService;
    }

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
    public com.kriterion.dto.auth.AuthResponse login(com.kriterion.dto.auth.LoginRequest request) {
        return loginInternal(request.getEmail(), request.getPassword(), request.getDevice());
    }

    @Transactional
    public com.kriterion.dto.auth.AuthResponse login(String email, String password) {
        return loginInternal(email, password, null);
    }

    private com.kriterion.dto.auth.AuthResponse loginInternal(String email, String password, com.kriterion.dto.mobile.DeviceMetadata device) {
        String normalized = email.toLowerCase().trim();
        String clientIp = getClientIp();

        // Attempt to find user
        java.util.Optional<User> userOpt = userRepository.findByEmail(normalized);

        if (userOpt.isEmpty()) {
            PasswordSecurityUtil.verifyPassword(password, "$2a$12$dummy", passwordEncoder);
            throw new ApiException(PasswordSecurityUtil.getGenericAuthError(), HttpStatus.UNAUTHORIZED);
        }

        User user = userOpt.get();

        if (!PasswordSecurityUtil.verifyPassword(password, user.getPasswordHash(), passwordEncoder)) {
            throw new ApiException(PasswordSecurityUtil.getGenericAuthError(), HttpStatus.UNAUTHORIZED);
        }

        String subject = String.valueOf(user.getId());
        String accessToken = jwtTokenService.generateAccessToken(subject);
        String refreshToken;

        // MOBILITY: Create session if device info is provided
        if (device != null) {
            if (device.getIpAddress() == null) device.setIpAddress(clientIp);
            refreshToken = sessionService.createSession(user, device);
        } else {
            // Web/Standard Refresh Token
            refreshToken = jwtTokenService.generateRefreshToken(subject);
            com.kriterion.entity.RefreshToken rt = new com.kriterion.entity.RefreshToken();
            rt.setUser(user);
            rt.setToken(refreshToken);
            rt.setExpiryDate(java.time.Instant.now().plus(jwtProperties.getRefreshTokenExpirationDays(), java.time.temporal.ChronoUnit.DAYS));
            rt.setRevoked(false);
            refreshTokenRepository.save(rt);
        }

        rateLimiter.reset(clientIp);
        log.info("User logged in successfully: id={}, ip={}, mobile={}", user.getId(), clientIp, device != null);

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
            if (rt.getSession() != null) {
                sessionService.revokeSession(rt.getSession().getId());
            }
            refreshTokenRepository.save(rt);
            log.info("Refresh token revoked for userId={}", rt.getUser().getId());
        });
    }

    @Transactional
    public com.kriterion.dto.auth.TokenRefreshResponse refreshAccessToken(String refreshToken) {
        String newRefreshToken = sessionService.refreshSession(refreshToken);
        
        com.kriterion.entity.RefreshToken rt = refreshTokenRepository.findByToken(newRefreshToken)
                .orElseThrow(() -> new ApiException("Token refresh failed", HttpStatus.INTERNAL_SERVER_ERROR));

        String accessToken = jwtTokenService.generateAccessToken(String.valueOf(rt.getUser().getId()));
        
        return com.kriterion.dto.auth.TokenRefreshResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();
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
