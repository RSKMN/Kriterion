package com.kriterion.notification;

import com.kriterion.dto.notification.NotificationResponse;
import com.kriterion.entity.Notification;
import com.kriterion.exception.ApiException;
import com.kriterion.repository.NotificationRepository;
import com.kriterion.security.util.AuthenticationUtil;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsForUser() {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ApiException("Notification not found", HttpStatus.NOT_FOUND));

        if (!notification.getUserId().equals(userId)) {
            throw new ApiException("Unauthorized to access this notification", HttpStatus.FORBIDDEN);
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        notifications.stream()
                .filter(n -> !n.getIsRead())
                .forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Override
    @Transactional
    public void deleteNotification(Long id) {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ApiException("Notification not found", HttpStatus.NOT_FOUND));

        if (!notification.getUserId().equals(userId)) {
            throw new ApiException("Unauthorized to delete this notification", HttpStatus.FORBIDDEN);
        }

        notificationRepository.delete(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount() {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .severity(notification.getSeverity())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
