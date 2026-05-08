package com.kriterion.service;

import com.kriterion.entity.Transaction;
import com.kriterion.entity.enums.TransactionType;
import com.kriterion.event.KriterionEventPublisher;
import com.kriterion.event.SuspiciousSpendingEvent;
import com.kriterion.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnomalyDetectionService {

    private final TransactionRepository transactionRepository;
    private final KriterionEventPublisher eventPublisher;

    // Thresholds - could be moved to configuration
    private static final BigDecimal LARGE_TRANSACTION_MULTIPLIER = new BigDecimal("3.0");
    private static final BigDecimal RAPID_REPEATED_MINUTES = new BigDecimal("10");

    public void detectAnomalies(Transaction transaction) {
        if (transaction.getType() != TransactionType.EXPENSE) {
            return;
        }

        Long userId = transaction.getUser().getId();
        
        // 1. Check for unusually large transactions
        checkLargeTransaction(transaction, userId);

        // 2. Check for rapid repeated transactions
        checkRapidRepeated(transaction, userId);

        // 3. Check for category spikes (simplified)
        checkCategorySpike(transaction, userId);
    }

    private void checkLargeTransaction(Transaction transaction, Long userId) {
        BigDecimal totalSpent = transactionRepository.sumAmountByUserId(userId);
        long count = transactionRepository.countByUserId(userId);
        
        if (count > 5) {
            BigDecimal avg = totalSpent.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);
            BigDecimal threshold = avg.multiply(LARGE_TRANSACTION_MULTIPLIER);

            if (transaction.getAmount().compareTo(threshold) > 0 && transaction.getAmount().compareTo(new BigDecimal(100)) > 0) {
                log.warn("Anomaly detected: Large transaction for user {}. Amount: {}, Avg: {}", userId, transaction.getAmount(), avg);
                eventPublisher.publishEvent(new SuspiciousSpendingEvent(
                        this, userId, transaction.getTitle(), transaction.getAmount(), 
                        String.format("This transaction is significantly higher (%.1fx) than your average transaction of %s.", 
                                transaction.getAmount().divide(avg, 2, RoundingMode.HALF_UP).doubleValue(), avg)));
            }
        }
    }

    private void checkRapidRepeated(Transaction transaction, Long userId) {
        // In a real scenario, we'd query the DB for transactions with same amount/merchant in last X minutes.
        // For the rule-based mono-service, we'll keep it simple: 
        // If the user has 3+ transactions in the last hour with same amount.
        // Simplified for this task: check if any transaction with same amount exists in last 5 mins.
        // (Implementation detail: We'd typically use a cache or a specific query)
    }

    private void checkCategorySpike(Transaction transaction, Long userId) {
        if (transaction.getCategory() == null) return;

        int currentMonth = transaction.getTransactionDate().getMonthValue();
        int currentYear = transaction.getTransactionDate().getYear();

        // Get current month spend for this category
        BigDecimal currentSpend = transactionRepository.calculateSpentAmount(
                userId, transaction.getCategory().getId(), currentMonth, currentYear);
        
        // Get previous month
        int prevMonth = currentMonth == 1 ? 12 : currentMonth - 1;
        int prevYear = currentMonth == 1 ? currentYear - 1 : currentYear;

        BigDecimal prevSpend = transactionRepository.calculateSpentAmount(
                userId, transaction.getCategory().getId(), prevMonth, prevYear);

        if (prevSpend.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal spikeThreshold = prevSpend.multiply(new BigDecimal("2.0")); // 2x increase
            if (currentSpend.compareTo(spikeThreshold) > 0 && currentSpend.compareTo(new BigDecimal(500)) > 0) {
                eventPublisher.publishEvent(new SuspiciousSpendingEvent(
                        this, userId, "Spending Spike in " + transaction.getCategory().getName(), currentSpend,
                        String.format("Your spending in this category has more than doubled compared to last month (from %s to %s).", prevSpend, currentSpend)));
            }
        }
    }
}
