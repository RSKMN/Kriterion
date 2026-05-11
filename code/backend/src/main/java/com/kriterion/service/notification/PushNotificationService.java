package com.kriterion.service.notification;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class PushNotificationService {

    public void sendPushNotification(String token, String title, String body, Map<String, String> data) {
        try {
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(notification)
                    .putAllData(data)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Sent message to token: {}, response: {}", token, response);
        } catch (Exception e) {
            log.error("Failed to send push notification to token {}: {}", token, e.getMessage());
            // Token might be invalid, but we'll handle invalidation in NotificationDeliveryService
        }
    }
}
