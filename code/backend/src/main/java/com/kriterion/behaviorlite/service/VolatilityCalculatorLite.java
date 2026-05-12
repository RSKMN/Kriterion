package com.kriterion.behaviorlite.service;

import com.kriterion.entity.Transaction;
import com.kriterion.entity.enums.TransactionType;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class VolatilityCalculatorLite {

    public double calculateWeeklySpendingVolatility(List<Transaction> transactions) {
        List<Transaction> expenses = filterExpenses(transactions);
        if (expenses.size() < 2) {
            return 0.0;
        }

        List<Double> weeklyTotals = expenses.stream()
                .collect(Collectors.groupingBy(transaction -> startOfWeek(transaction.getTransactionDate()), Collectors.summingDouble(transaction -> safeAmount(transaction.getAmount()))))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .toList();

        if (weeklyTotals.size() < 2) {
            return 0.0;
        }

        double mean = weeklyTotals.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        if (mean == 0.0) {
            return 0.0;
        }

        double variance = weeklyTotals.stream()
                .mapToDouble(value -> Math.pow(value - mean, 2))
                .average()
                .orElse(0.0);

        return round(Math.sqrt(variance) / mean);
    }

    public List<com.kriterion.behaviorlite.dto.SeriesPointResponse> buildWeeklySpendingSeries(List<Transaction> transactions) {
        List<Transaction> expenses = filterExpenses(transactions);
        if (expenses.isEmpty()) {
            return List.of();
        }

        return expenses.stream()
                .collect(Collectors.groupingBy(transaction -> startOfWeek(transaction.getTransactionDate()), Collectors.summingDouble(transaction -> safeAmount(transaction.getAmount()))))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> com.kriterion.behaviorlite.dto.SeriesPointResponse.builder()
                        .label(entry.getKey().toString())
                        .value(round(entry.getValue()))
                        .build())
                .toList();
    }

    private List<Transaction> filterExpenses(List<Transaction> transactions) {
        if (transactions == null) {
            return List.of();
        }
        return transactions.stream()
                .filter(transaction -> transaction.getType() == TransactionType.EXPENSE)
                .sorted(Comparator.comparing(Transaction::getTransactionDate))
                .collect(Collectors.toList());
    }

    private LocalDate startOfWeek(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    private double safeAmount(BigDecimal amount) {
        return amount == null ? 0.0 : amount.doubleValue();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}