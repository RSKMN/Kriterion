package com.kriterion.notification;

import com.kriterion.dto.notification.NotificationResponse;
import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getNotificationsForUser();
    void markAsRead(Long id);
    void markAllAsRead();
    void deleteNotification(Long id);
    long getUnreadCount();
}
