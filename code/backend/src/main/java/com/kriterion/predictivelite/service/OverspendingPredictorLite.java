package com.kriterion.predictivelite.service;

import com.kriterion.entity.Transaction;
import com.kriterion.entity.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class OverspendingPredictorLite {

    public record OverspendingForecast(String risk, Integer daysRemaining, Double confidence) {}

    public OverspendingForecast predictOverspending(List<Transaction> transactions) {
        if (transactions == null || transactions.size() < 10) {
            return new OverspendingForecast("low", null, 0.25);
        }

        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysAgo = today.minusDays(30);

        List<Transaction> recentExpenses = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .filter(t -> !t.getTransactionDate().isBefore(thirtyDaysAgo))
                .collect(Collectors.toList());

        if (recentExpenses.isEmpty()) {
            return new OverspendingForecast("low", null, 0.2);
        }

        List<Transaction> recentIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .filter(t -> !t.getTransactionDate().isBefore(thirtyDaysAgo))
                .collect(Collectors.toList());

        double totalExpenses = recentExpenses.stream()
                .mapToDouble(t -> safeAmount(t.getAmount()))
                .sum();
        double totalIncome = recentIncome.stream()
                .mapToDouble(t -> safeAmount(t.getAmount()))
                .sum();

        if (totalIncome <= 0) {
            return new OverspendingForecast("moderate", null, 0.35);
        }

        double burnRate = totalExpenses / totalIncome;

        // Calculate daily average spending
        long daySpan = Math.max(1, java.time.temporal.ChronoUnit.DAYS.between(
                recentExpenses.stream().map(Transaction::getTransactionDate).min(java.time.LocalDate::compareTo).orElse(today),
                today
        ));
        double dailyAverage = totalExpenses / Math.max(1, daySpan);

        // Simple heuristic: if burn rate > 0.9, rising risk
        String risk;
        if (burnRate > 1.1) {
            risk = "high";
        } else if (burnRate > 0.85) {
            risk = "moderate";
        } else {
            risk = "low";
        }

        // Estimate days remaining at current burn rate
        Integer daysRemaining = null;
        if (dailyAverage > 0 && totalIncome > totalExpenses) {
            double remainingBudget = totalIncome - totalExpenses;
            daysRemaining = (int) Math.max(1, remainingBudget / dailyAverage);
        }

        double confidence = Math.min(1.0, recentExpenses.size() / 30.0);

        return new OverspendingForecast(risk, daysRemaining, confidence);
    }

    private double safeAmount(BigDecimal amount) {
        return amount == null ? 0.0 : amount.doubleValue();
    }
}
