package com.kriterion.scheduler;

import com.kriterion.service.RecurringTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecurringTransactionScheduler {

    private final RecurringTransactionService recurringTransactionService;

    @Scheduled(cron = "${scheduler.recurring-transactions.cron:0 0 1 * * *}")
    public void processRecurringTransactions() {
        recurringTransactionService.executeRecurringTransactions();
    }
}
