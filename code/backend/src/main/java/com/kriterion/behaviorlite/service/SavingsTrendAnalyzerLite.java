package com.kriterion.behaviorlite.service;

import com.kriterion.behaviorlite.dto.SeriesPointResponse;
import com.kriterion.behaviorlite.service.SavingsTrendAnalyzerLite.SavingsTrendResult;
import com.kriterion.entity.Transaction;
import com.kriterion.entity.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class SavingsTrendAnalyzerLite {

    public SavingsTrendResult analyzeTrend(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return new SavingsTrendResult(0.0, "stable", List.of());
        }

        LocalDate cutoff = LocalDate.now().minusDays(30);
        List<Transaction> recentTransactions = transactions.stream()
                .filter(transaction -> !transaction.getTransactionDate().isBefore(cutoff))
                .sorted(Comparator.comparing(Transaction::getTransactionDate))
                .toList();

        if (recentTransactions.isEmpty()) {
            return new SavingsTrendResult(0.0, "stable", List.of());
        }

        Map<LocalDate, Double> dailyNet = new LinkedHashMap<>();
        for (Transaction transaction : recentTransactions) {
            double delta = transaction.getType() == TransactionType.INCOME ? safeAmount(transaction.getAmount()) : -safeAmount(transaction.getAmount());
            dailyNet.merge(transaction.getTransactionDate(), delta, Double::sum);
        }

        List<SeriesPointResponse> series = new ArrayList<>();
        double cumulative = 0.0;
        for (Map.Entry<LocalDate, Double> entry : dailyNet.entrySet()) {
            cumulative += entry.getValue();
            series.add(SeriesPointResponse.builder()
                    .label(entry.getKey().toString())
                    .value(round(cumulative))
                    .build());
        }

        if (series.size() < 2) {
            return new SavingsTrendResult(0.0, "stable", series);
        }

        double slope = (series.get(series.size() - 1).getValue() - series.get(0).getValue()) / (series.size() - 1);
        String label = slope > 0.5 ? "improving" : slope < -0.5 ? "declining" : "stable";

        return new SavingsTrendResult(round(slope), label, series);
    }

    private double safeAmount(BigDecimal amount) {
        return amount == null ? 0.0 : amount.doubleValue();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public record SavingsTrendResult(double slope, String label, List<SeriesPointResponse> series) {
    }
}