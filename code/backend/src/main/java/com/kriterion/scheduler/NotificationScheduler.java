package com.kriterion.scheduler;

import com.kriterion.repository.UserRepository;
import com.kriterion.service.BudgetService;
import com.kriterion.service.RecurringTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final BudgetService budgetService;
    private final RecurringTransactionService recurringTransactionService;
    private final UserRepository userRepository;

    @Scheduled(cron = "${scheduler.notifications.cron:0 0 */4 * * *}") // Every 4 hours
    public void processNotifications() {
        log.info("Starting notification processing cycle");
        
        // Process recurring transaction reminders
        recurringTransactionService.sendReminders();
        
        // Process budget alerts for all active users
        userRepository.findAll().forEach(user -> {
            try {
                budgetService.evaluateBudgets(user.getId());
            } catch (Exception e) {
                log.error("Failed to evaluate budgets for user {}: {}", user.getId(), e.getMessage());
            }
        });
        
        log.info("Finished notification processing cycle");
    }
}
