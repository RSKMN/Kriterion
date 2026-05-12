package com.kriterion.predictivelite.service;

import com.kriterion.entity.Transaction;
import com.kriterion.entity.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class EndOfMonthRiskAnalyzerLite {

    public record EndOfMonthRisk(Double likelihood, Double confidence) {}

    public EndOfMonthRisk analyzeEndOfMonthRisk(List<Transaction> transactions) {
        if (transactions == null || transactions.size() < 10) {
            return new EndOfMonthRisk(0.0, 0.2);
        }

        LocalDate today = LocalDate.now();
        LocalDate start = today.minusMonths(3);

        // Analyze last 3 months for end-of-month spending spikes
        int spikeCount = 0;
        int monthCount = 0;

        for (int m = 0; m < 3; m++) {
            LocalDate monthStart = today.minusMonths(m).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
            LocalDate lastWeekStart = monthEnd.minusDays(7);

            double lastWeekExpenses = transactions.stream()
                    .filter(t -> t.getType() == TransactionType.EXPENSE)
                    .filter(t -> !t.getTransactionDate().isBefore(lastWeekStart))
                    .filter(t -> !t.getTransactionDate().isAfter(monthEnd))
                    .mapToDouble(t -> safeAmount(t.getAmount()))
                    .sum();

            double monthExpenses = transactions.stream()
                    .filter(t -> t.getType() == TransactionType.EXPENSE)
                    .filter(t -> !t.getTransactionDate().isBefore(monthStart))
                    .filter(t -> !t.getTransactionDate().isAfter(monthEnd))
                    .mapToDouble(t -> safeAmount(t.getAmount()))
                    .sum();

            if (monthExpenses > 0 && lastWeekExpenses / monthExpenses > 0.3) {
                spikeCount++;
            }
            monthCount++;
        }

        double likelihood = monthCount > 0 ? (double) spikeCount / monthCount : 0.0;
        double confidence = Math.min(1.0, transactions.size() / 90.0);

        return new EndOfMonthRisk(likelihood, confidence);
    }

    private double safeAmount(BigDecimal amount) {
        return amount == null ? 0.0 : amount.doubleValue();
    }
}
