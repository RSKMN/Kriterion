package com.kriterion.event;

import com.kriterion.entity.Notification;
import com.kriterion.entity.enums.NotificationSeverity;
import com.kriterion.entity.enums.NotificationType;
import com.kriterion.repository.NotificationRepository;
import com.kriterion.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @EventListener
    @Transactional
    public void handleBudgetExceeded(BudgetExceededEvent event) {
        String title = "Budget Exceeded: " + event.getCategoryName();
        String message = String.format("You have exceeded your budget for %s. Limit: %s, Spent: %s",
                event.getCategoryName(), event.getLimit(), event.getSpent());

        saveAndNotify(event.getUserId(), title, message, NotificationType.BUDGET_ALERT, NotificationSeverity.CRITICAL);
        
        Map<String, Object> model = new HashMap<>();
        model.put("title", title);
        model.put("message", message);
        model.put("categoryName", event.getCategoryName());
        model.put("limit", event.getLimit());
        model.put("spent", event.getSpent());
        model.put("percentage", "100+");
        model.put("severity", "CRITICAL");
        
        emailService.sendHtmlNotificationEmail(event.getUserId(), title, "budget-alert", model);
    }

    @EventListener
    @Transactional
    public void handleBudgetWarning(BudgetWarningEvent event) {
        String title = "Budget Warning: " + event.getCategoryName();
        String message = String.format("You have reached %d%% of your budget for %s. Current usage: %s%%",
                event.getThreshold(), event.getCategoryName(), event.getPercentage());

        saveAndNotify(event.getUserId(), title, message, NotificationType.BUDGET_ALERT, NotificationSeverity.WARNING);
        
        Map<String, Object> model = new HashMap<>();
        model.put("title", title);
        model.put("message", message);
        model.put("categoryName", event.getCategoryName());
        model.put("limit", "N/A"); // Ideally we pass limit here too
        model.put("spent", "N/A");
        model.put("percentage", event.getPercentage());
        model.put("severity", "WARNING");
        
        emailService.sendHtmlNotificationEmail(event.getUserId(), title, "budget-alert", model);
    }

    @EventListener
    @Transactional
    public void handleRecurringReminder(RecurringTransactionReminderEvent event) {
        String title = "Upcoming Bill: " + event.getDescription();
        String message = String.format("Your recurring transaction '%s' for %s is due on %s",
                event.getDescription(), event.getAmount(), event.getDueDate());

        saveAndNotify(event.getUserId(), title, message, NotificationType.RECURRING_REMINDER, NotificationSeverity.INFO);
        
        Map<String, Object> model = new HashMap<>();
        model.put("title", title);
        model.put("message", message);
        model.put("description", event.getDescription());
        model.put("amount", event.getAmount());
        model.put("dueDate", event.getDueDate());
        
        emailService.sendHtmlNotificationEmail(event.getUserId(), title, "recurring-reminder", model);
    }

    @EventListener
    @Transactional
    public void handleSuspiciousSpending(SuspiciousSpendingEvent event) {
        String title = "Suspicious Spending Alert";
        String message = String.format("Suspicious transaction detected: '%s' for %s. Reason: %s",
                event.getDescription(), event.getAmount(), event.getReason());

        saveAndNotify(event.getUserId(), title, message, NotificationType.SUSPICIOUS_SPENDING, NotificationSeverity.CRITICAL);
        
        Map<String, Object> model = new HashMap<>();
        model.put("title", title);
        model.put("message", message);
        model.put("description", event.getDescription());
        model.put("amount", event.getAmount());
        model.put("reason", event.getReason());
        
        emailService.sendHtmlNotificationEmail(event.getUserId(), title, "suspicious-spending", model);
    }

    private void saveAndNotify(Long userId, String title, String message, NotificationType type, NotificationSeverity severity) {
        Notification notification = Notification.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type(type)
                .severity(severity)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        log.info("Saved notification for user: {}. Type: {}", userId, type);
    }
}
