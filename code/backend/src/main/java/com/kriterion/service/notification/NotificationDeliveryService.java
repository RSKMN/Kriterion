package com.kriterion.service.notification;

import com.kriterion.entity.FcmToken;
import com.kriterion.entity.User;
import com.kriterion.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDeliveryService {

    private final FcmTokenRepository fcmTokenRepository;
    private final PushNotificationService pushNotificationService;

    @Transactional(readOnly = true)
    public void sendToUser(User user, String title, String body, Map<String, String> data) {
        List<FcmToken> tokens = fcmTokenRepository.findByUserAndActiveTrue(user);
        if (tokens.isEmpty()) {
            log.info("No active FCM tokens found for user {}", user.getId());
            return;
        }

        for (FcmToken token : tokens) {
            pushNotificationService.sendPushNotification(token.getToken(), title, body, data);
        }
    }

    @Transactional
    public void registerToken(User user, String token, String deviceId, String platform) {
        fcmTokenRepository.findByToken(token).ifPresentOrElse(
            existing -> {
                existing.setActive(true);
                existing.setDeviceId(deviceId);
                existing.setPlatform(platform);
                existing.setLastValidatedAt(java.time.LocalDateTime.now());
                fcmTokenRepository.save(existing);
            },
            () -> {
                FcmToken fcmToken = FcmToken.builder()
                        .user(user)
                        .token(token)
                        .deviceId(deviceId)
                        .platform(platform)
                        .lastValidatedAt(java.time.LocalDateTime.now())
                        .active(true)
                        .build();
                fcmTokenRepository.save(fcmToken);
            }
        );
    }

    @Transactional
    public void unregisterToken(String token) {
        fcmTokenRepository.findByToken(token).ifPresent(t -> {
            t.setActive(false);
            fcmTokenRepository.save(t);
        });
    }
}
