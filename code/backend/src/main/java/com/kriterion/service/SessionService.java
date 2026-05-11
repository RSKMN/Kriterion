package com.kriterion.service;

import com.kriterion.dto.mobile.DeviceMetadata;
import com.kriterion.entity.MobileSession;
import com.kriterion.entity.RefreshToken;
import com.kriterion.entity.User;
import com.kriterion.exception.UnauthorizedException;
import com.kriterion.repository.MobileSessionRepository;
import com.kriterion.repository.RefreshTokenRepository;
import com.kriterion.security.jwt.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService {

    private final MobileSessionRepository sessionRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenService jwtTokenService;

    @Transactional
    public String createSession(User user, DeviceMetadata metadata) {
        // Deactivate existing session for this device if it exists
        sessionRepository.findByUserAndDeviceId(user, metadata.getDeviceId())
                .ifPresent(s -> {
                    s.setActive(false);
                    refreshTokenRepository.deleteBySessionId(s.getId());
                    sessionRepository.save(s);
                });

        MobileSession session = new MobileSession();
        session.setUser(user);
        session.setDeviceId(metadata.getDeviceId());
        session.setDeviceName(metadata.getDeviceName());
        session.setPlatform(metadata.getPlatform());
        session.setOsVersion(metadata.getOsVersion());
        session.setAppVersion(metadata.getAppVersion());
        session.setIpAddress(metadata.getIpAddress());
        session.setLastActiveAt(java.time.LocalDateTime.now());
        session.setActive(true);

        session = sessionRepository.save(session);

        return generateAndSaveRefreshToken(user, session);
    }

    @Transactional
    public String refreshSession(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshToken.isRevoked() || refreshToken.isExpired()) {
            // Suspicious activity! Revoke the whole session
            if (refreshToken.getSession() != null) {
                revokeSession(refreshToken.getSession().getId());
            }
            throw new UnauthorizedException("Token revoked or expired");
        }

        // Token Rotation: Revoke old one, issue new one
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        MobileSession session = refreshToken.getSession();
        session.setLastActiveAt(java.time.LocalDateTime.now());
        sessionRepository.save(session);

        return generateAndSaveRefreshToken(refreshToken.getUser(), session);
    }

    @Transactional
    public void revokeSession(Long sessionId) {
        MobileSession session = sessionRepository.findById(sessionId)
                .orElse(null);
        if (session != null) {
            session.setActive(false);
            sessionRepository.save(session);
            refreshTokenRepository.deleteBySessionId(sessionId);
            log.info("Session {} revoked", sessionId);
        }
    }

    @Transactional
    public void revokeAllSessions(Long userId) {
        // We'll use a direct query or fetch all
        List<MobileSession> sessions = sessionRepository.findByUserIdAndActiveTrue(userId);
        for (MobileSession session : sessions) {
            session.setActive(false);
        }
        sessionRepository.saveAll(sessions);
        refreshTokenRepository.deleteByUserId(userId);
        log.info("All sessions for user {} revoked", userId);
    }

    private String generateAndSaveRefreshToken(User user, MobileSession session) {
        String token = java.util.UUID.randomUUID().toString();
        
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setSession(session);
        refreshToken.setExpiryDate(java.time.Instant.now().plus(30, java.time.temporal.ChronoUnit.DAYS));
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);
        return token;
    }

    public List<MobileSession> getActiveSessions(Long userId) {
        return sessionRepository.findByUserIdAndActiveTrue(userId);
    }
}
