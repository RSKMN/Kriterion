package com.kriterion.service.mobile;

import com.kriterion.dto.mobile.DeviceMetadata;
import com.kriterion.dto.mobile.FcmTokenRegistrationRequest;
import com.kriterion.entity.MobileSession;
import com.kriterion.entity.User;
import com.kriterion.repository.MobileSessionRepository;
import com.kriterion.repository.UserRepository;
import com.kriterion.security.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MobileService {

    private final MobileSessionRepository mobileSessionRepository;
    private final UserRepository userRepository;
    private final com.kriterion.service.notification.NotificationDeliveryService notificationDeliveryService;

    @Transactional
    public void registerDevice(DeviceMetadata metadata) {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        User user = userRepository.findById(userId).orElseThrow();

        MobileSession session = mobileSessionRepository.findByUserAndDeviceId(user, metadata.getDeviceId())
                .orElseGet(() -> {
                    MobileSession s = new MobileSession();
                    s.setUser(user);
                    s.setDeviceId(metadata.getDeviceId());
                    return s;
                });

        session.setDeviceName(metadata.getDeviceName());
        session.setPlatform(metadata.getPlatform());
        session.setOsVersion(metadata.getOsVersion());
        session.setAppVersion(metadata.getAppVersion());
        session.setLastActiveAt(java.time.LocalDateTime.now());
        session.setActive(true);

        mobileSessionRepository.save(session);
        log.info("Registered/Updated mobile device {} for user {}", metadata.getDeviceId(), userId);
    }

    @Transactional
    public void registerFcmToken(FcmTokenRegistrationRequest request) {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        User user = userRepository.findById(userId).orElseThrow();

        // 1. Sync with MobileSession if exists
        if (request.getDeviceId() != null) {
            mobileSessionRepository.findByUserAndDeviceId(user, request.getDeviceId()).ifPresent(session -> {
                session.setFcmToken(request.getToken());
                session.setLastActiveAt(java.time.LocalDateTime.now());
                mobileSessionRepository.save(session);
            });
        }

        // 2. Delegate to dedicated FCM service
        notificationDeliveryService.registerToken(user, request.getToken(), request.getDeviceId(), request.getPlatform());
        log.info("FCM token registered for user {}", userId);
    }

    @Transactional
    public void unregisterFcmToken(String token) {
        // 1. Clean up from sessions
        mobileSessionRepository.findByFcmToken(token).ifPresent(session -> {
            session.setFcmToken(null);
            mobileSessionRepository.save(session);
        });

        // 2. Delegate to dedicated FCM service
        notificationDeliveryService.unregisterToken(token);
        log.info("FCM token unregistered");
    }

    @Transactional
    public void logoutDevice(String deviceId) {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        User user = userRepository.findById(userId).orElseThrow();

        mobileSessionRepository.findByUserAndDeviceId(user, deviceId).ifPresent(session -> {
            session.setActive(false);
            if (session.getFcmToken() != null) {
                notificationDeliveryService.unregisterToken(session.getFcmToken());
                session.setFcmToken(null);
            }
            mobileSessionRepository.save(session);
            log.info("Logged out mobile device {} for user {}", deviceId, userId);
        });
    }
}
