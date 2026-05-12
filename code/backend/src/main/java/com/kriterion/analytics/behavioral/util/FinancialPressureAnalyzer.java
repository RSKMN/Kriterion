package com.kriterion.analytics.behavioral.util;

import com.kriterion.entity.Budget;
import com.kriterion.entity.Transaction;
import com.kriterion.repository.BudgetRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FinancialPressureAnalyzer {

    private final BudgetRepository budgetRepository;

    public double calculateUnresolvedBudgetPressure(Long userId) {
        LocalDate now = LocalDate.now();
        List<Budget> budgets = budgetRepository.findByUserIdAndMonthAndYear(userId, now.getMonthValue(), now.getYear());
        
        if (budgets.isEmpty()) return 0.0;
        
        double pressureTotal = 0.0;
        for (Budget budget : budgets) {
            if (budget.getMonthlyLimit().signum() > 0) {
                double ratio = budget.getCurrentSpent().doubleValue() / budget.getMonthlyLimit().doubleValue();
                if (ratio > 0.8) {
                    pressureTotal += (ratio - 0.8) * 5; // Rapidly increase pressure after 80%
                }
            }
        }
        
        return Math.min(1.0, pressureTotal / budgets.size());
    }

    public double calculateMicroTransactionLoad(List<Transaction> transactions) {
        if (transactions.isEmpty()) return 0.0;
        
        // Define micro-transaction as < 200 INR (or equivalent)
        long microCount = transactions.stream()
            .filter(t -> t.getAmount().compareTo(BigDecimal.valueOf(200)) < 0)
            .count();
            
        return (double) microCount / transactions.size();
    }

    public double calculateSubscriptionLoad(List<Transaction> transactions) {
        long recurringCount = transactions.stream()
            .filter(t -> t.getIsRecurring() != null && t.getIsRecurring())
            .count();
            
        // Assuming more than 10 recurring transactions is a high load
        return Math.min(1.0, recurringCount / 10.0);
    }
}
