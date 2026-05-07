package com.kriterion.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationScheduler {

    @Scheduled(cron = "${scheduler.notifications.cron:0 */15 * * * *}")
    public void processNotifications() {
        // Placeholder for notification delivery work.
    }
}
