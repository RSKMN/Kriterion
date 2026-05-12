package com.kriterion.predictivelite.service;

import com.kriterion.entity.Transaction;
import com.kriterion.entity.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionCreepDetectorLite {

    public record SubscriptionCreepForecast(String trend, Double recentRecurringLoad, Double previousRecurringLoad, Double confidence) {}

    public SubscriptionCreepForecast detectSubscriptionCreep(List<Transaction> transactions) {
        if (transactions == null || transactions.size() < 5) {
            return new SubscriptionCreepForecast("stable", 0.0, 0.0, 0.2);
        }

        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysAgo = today.minusDays(30);
        LocalDate sixtyDaysAgo = today.minusDays(60);

        // Recent recurring expenses
        double recentRecurring = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .filter(t -> Boolean.TRUE.equals(t.getIsRecurring()))
                .filter(t -> !t.getTransactionDate().isBefore(thirtyDaysAgo))
                .mapToDouble(t -> safeAmount(t.getAmount()))
                .sum();

        // Recent income
        double recentIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .filter(t -> !t.getTransactionDate().isBefore(thirtyDaysAgo))
                .mapToDouble(t -> safeAmount(t.getAmount()))
                .sum();

        // Previous recurring expenses
        double previousRecurring = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .filter(t -> Boolean.TRUE.equals(t.getIsRecurring()))
                .filter(t -> !t.getTransactionDate().isBefore(sixtyDaysAgo))
                .filter(t -> t.getTransactionDate().isBefore(thirtyDaysAgo))
                .mapToDouble(t -> safeAmount(t.getAmount()))
                .sum();

        // Previous income
        double previousIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .filter(t -> !t.getTransactionDate().isBefore(sixtyDaysAgo))
                .filter(t -> t.getTransactionDate().isBefore(thirtyDaysAgo))
                .mapToDouble(t -> safeAmount(t.getAmount()))
                .sum();

        // Calculate ratios
        double recentRatio = recentIncome > 0 ? recentRecurring / recentIncome : 0.0;
        double previousRatio = previousIncome > 0 ? previousRecurring / previousIncome : 0.0;

        String trend;
        if (recentRatio > previousRatio * 1.1 && recentRatio > 0.1) {
            trend = "increasing";
        } else if (recentRatio < previousRatio * 0.9) {
            trend = "decreasing";
        } else {
            trend = "stable";
        }

        double confidence = Math.min(1.0, (double) transactions.stream()
                .filter(t -> Boolean.TRUE.equals(t.getIsRecurring()))
                .count() / 10.0);

        return new SubscriptionCreepForecast(trend, recentRatio, previousRatio, confidence);
    }

    private double safeAmount(BigDecimal amount) {
        return amount == null ? 0.0 : amount.doubleValue();
    }
}
