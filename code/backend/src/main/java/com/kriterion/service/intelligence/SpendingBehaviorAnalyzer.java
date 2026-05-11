package com.kriterion.service.intelligence;

import com.kriterion.entity.FinancialEvent;
import com.kriterion.entity.Transaction;
import com.kriterion.repository.FinancialEventRepository;
import com.kriterion.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpendingBehaviorAnalyzer {

    private final TransactionRepository transactionRepository;
    private final FinancialEventRepository eventRepository;

    public void analyze(Transaction transaction) {
        detectShoppingBurst(transaction);
        detectImpulsiveSpending(transaction);
        detectSalaryCredit(transaction);
    }

    private void detectShoppingBurst(Transaction transaction) {
        // Check if there were many transactions in the last 24 hours
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        List<Transaction> recent = transactionRepository.findAllByUserId(transaction.getUser().getId())
                .stream()
                .filter(t -> t.getCreatedAt().isAfter(yesterday))
                .toList();

        if (recent.size() >= 5) {
            transaction.getTags().add("Shopping Burst");
            createEvent(transaction.getUser().getId(), "SHOPPING_BURST", 
                "Shopping Burst Detected", "You've made " + recent.size() + " transactions in the last 24 hours.", "WARNING");
        }
    }

    private void detectImpulsiveSpending(Transaction transaction) {
        // High amount relative to average for this category
        if (transaction.getAmount().doubleValue() > 5000) { // Arbitrary threshold for demo
            transaction.getTags().add("Large Purchase");
        }
    }

    private void detectSalaryCredit(Transaction transaction) {
        String title = transaction.getTitle().toLowerCase();
        if (transaction.getType() == com.kriterion.entity.enums.TransactionType.INCOME && 
           (title.contains("salary") || title.contains("credit") || title.contains("payroll"))) {
            transaction.getTags().add("Salary");
            createEvent(transaction.getUser().getId(), "SALARY_CREDIT", 
                "Salary Credited", "Your salary of " + transaction.getAmount() + " has been recorded.", "SUCCESS");
        }
    }

    private void createEvent(Long userId, String type, String title, String description, String severity) {
        FinancialEvent event = FinancialEvent.builder()
                .userId(userId)
                .type(type)
                .title(title)
                .description(description)
                .severity(severity)
                .occurredAt(LocalDateTime.now())
                .isRead(false)
                .build();
        eventRepository.save(event);
    }
}
