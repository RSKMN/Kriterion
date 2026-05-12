package com.kriterion.predictivelite.service;

import com.kriterion.entity.Transaction;
import com.kriterion.entity.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SavingsForecastEngineLite {

    public record SavingsForecast(String trend, Double slope, Double confidence) {}

    public SavingsForecast forecastSavings(List<Transaction> transactions) {
        if (transactions == null || transactions.size() < 5) {
            return new SavingsForecast("stable", 0.0, 0.2);
        }

        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysAgo = today.minusDays(30);
        LocalDate sixtyDaysAgo = today.minusDays(60);

        // Recent 30-day window
        double netRecent = transactions.stream()
                .filter(t -> !t.getTransactionDate().isBefore(thirtyDaysAgo))
                .mapToDouble(t -> t.getType() == TransactionType.INCOME ? safeAmount(t.getAmount()) : -safeAmount(t.getAmount()))
                .sum();

        // Previous 30-day window
        double netPrevious = transactions.stream()
                .filter(t -> !t.getTransactionDate().isBefore(sixtyDaysAgo))
                .filter(t -> t.getTransactionDate().isBefore(thirtyDaysAgo))
                .mapToDouble(t -> t.getType() == TransactionType.INCOME ? safeAmount(t.getAmount()) : -safeAmount(t.getAmount()))
                .sum();

        // Calculate trend
        double slope = netRecent - netPrevious;
        String trend;
        if (slope > 10) {
            trend = "improving";
        } else if (slope < -10) {
            trend = "declining";
        } else {
            trend = "stable";
        }

        // Confidence based on data density
        long transactionCount = transactions.stream()
                .filter(t -> !t.getTransactionDate().isBefore(sixtyDaysAgo))
                .count();
        double confidence = Math.min(1.0, transactionCount / 30.0);

        return new SavingsForecast(trend, slope, confidence);
    }

    private double safeAmount(BigDecimal amount) {
        return amount == null ? 0.0 : amount.doubleValue();
    }
}
