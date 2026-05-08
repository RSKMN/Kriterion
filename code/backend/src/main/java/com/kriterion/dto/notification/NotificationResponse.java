package com.kriterion.dto.notification;

import com.kriterion.entity.enums.NotificationSeverity;
import com.kriterion.entity.enums.NotificationType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String title;
    private String message;
    private NotificationType type;
    private NotificationSeverity severity;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
