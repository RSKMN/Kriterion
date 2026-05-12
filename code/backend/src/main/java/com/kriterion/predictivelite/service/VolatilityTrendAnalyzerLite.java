package com.kriterion.predictivelite.service;

import com.kriterion.entity.Transaction;
import com.kriterion.entity.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class VolatilityTrendAnalyzerLite {

    public record VolatilityForecast(String trend, Double recentVolatility, Double previousVolatility, Double confidence) {}

    public VolatilityForecast analyzeVolatilityTrend(List<Transaction> transactions) {
        if (transactions == null || transactions.size() < 10) {
            return new VolatilityForecast("stable", 0.0, 0.0, 0.2);
        }

        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysAgo = today.minusDays(30);
        LocalDate sixtyDaysAgo = today.minusDays(60);

        // Recent volatility
        List<Double> recentExpenses = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .filter(t -> !t.getTransactionDate().isBefore(thirtyDaysAgo))
                .map(t -> safeAmount(t.getAmount()))
                .collect(Collectors.toList());

        // Previous volatility
        List<Double> previousExpenses = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .filter(t -> !t.getTransactionDate().isBefore(sixtyDaysAgo))
                .filter(t -> t.getTransactionDate().isBefore(thirtyDaysAgo))
                .map(t -> safeAmount(t.getAmount()))
                .collect(Collectors.toList());

        double recentVol = calculateVolatility(recentExpenses);
        double previousVol = calculateVolatility(previousExpenses);

        String trend;
        if (recentVol > previousVol * 1.15) {
            trend = "increasing";
        } else if (recentVol < previousVol * 0.85) {
            trend = "decreasing";
        } else {
            trend = "stable";
        }

        double confidence = Math.min(1.0, recentExpenses.size() / 30.0);

        return new VolatilityForecast(trend, recentVol, previousVol, confidence);
    }

    private double calculateVolatility(List<Double> amounts) {
        if (amounts == null || amounts.isEmpty()) {
            return 0.0;
        }

        if (amounts.size() == 1) {
            return 0.0;
        }

        double mean = amounts.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        if (mean == 0.0) {
            return 0.0;
        }

        double variance = amounts.stream()
                .mapToDouble(a -> Math.pow(a - mean, 2))
                .average()
                .orElse(0.0);

        return Math.sqrt(variance) / mean;
    }

    private double safeAmount(BigDecimal amount) {
        return amount == null ? 0.0 : amount.doubleValue();
    }
}
