package com.kriterion.service.intelligence;

import com.kriterion.entity.Transaction;
import com.kriterion.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecurringPatternDetector {

    private final TransactionRepository transactionRepository;

    public void detect(Transaction transaction) {
        if (transaction.getMerchantName() == null) return;

        // Check history for similar transactions (same merchant, roughly same amount)
        LocalDate monthAgo = transaction.getTransactionDate().minusMonths(3);
        List<Transaction> history = transactionRepository.findAllByUserId(transaction.getUser().getId())
                .stream()
                .filter(t -> t.getMerchantName() != null && t.getMerchantName().equalsIgnoreCase(transaction.getMerchantName()))
                .filter(t -> !t.getId().equals(transaction.getId()))
                .filter(t -> t.getTransactionDate().isAfter(monthAgo))
                .toList();

        if (history.size() >= 2) {
            // Found a pattern!
            if (isSubscriptionMerchant(transaction.getMerchantName())) {
                transaction.getTags().add("Subscription");
                transaction.setIsRecurring(true);
            } else {
                transaction.getTags().add("Recurring Bill");
            }
            log.info("Recurring pattern detected for merchant '{}'", transaction.getMerchantName());
        }
    }

    private boolean isSubscriptionMerchant(String merchant) {
        String lower = merchant.toLowerCase();
        return lower.contains("netflix") || lower.contains("spotify") || 
               lower.contains("amazon prime") || lower.contains("hotstar") || 
               lower.contains("google storage") || lower.contains("icloud");
    }
}
