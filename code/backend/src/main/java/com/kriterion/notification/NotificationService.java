package com.kriterion.notification;

import com.kriterion.dto.notification.NotificationResponse;
import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getNotificationsForUser();
    org.springframework.data.domain.Page<NotificationResponse> getNotifications(org.springframework.data.domain.Pageable pageable);
    void markAsRead(Long id);
    void markAllAsRead();
    void deleteNotification(Long id);
    long getUnreadCount();
}
