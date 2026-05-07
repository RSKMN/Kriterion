package com.kriterion.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RecurringTransactionScheduler {

    @Scheduled(cron = "${scheduler.recurring-transactions.cron:0 0 1 * * *}")
    public void processRecurringTransactions() {
        // Placeholder for recurring transaction execution.
    }
}
