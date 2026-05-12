package com.kriterion.behaviorlite.service;

import com.kriterion.behaviorlite.dto.SeriesPointResponse;
import com.kriterion.entity.Transaction;
import com.kriterion.entity.enums.TransactionType;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class FinancialRhythmAnalyzerLite {

    public Map<Integer, Double> buildHourlyDistribution(List<Transaction> transactions) {
        LinkedHashMap<Integer, Double> distribution = new LinkedHashMap<>();
        for (int hour = 0; hour < 24; hour++) {
            distribution.put(hour, 0.0);
        }

        if (transactions == null || transactions.isEmpty()) {
            return distribution;
        }

        long timedTransactions = transactions.stream().filter(transaction -> transaction.getTransactionTime() != null).count();
        if (timedTransactions == 0) {
            return distribution;
        }

        transactions.stream()
                .filter(transaction -> transaction.getTransactionTime() != null)
                .forEach(transaction -> distribution.compute(transaction.getTransactionTime().getHour(), (hour, value) -> (value == null ? 0.0 : value) + 1.0 / timedTransactions));

        return distribution;
    }

    public double calculateLateNightRatio(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return 0.0;
        }

        long expenseCount = transactions.stream().filter(transaction -> transaction.getType() == TransactionType.EXPENSE).count();
        if (expenseCount == 0) {
            return 0.0;
        }

        long lateNightExpenses = transactions.stream()
                .filter(transaction -> transaction.getType() == TransactionType.EXPENSE)
                .filter(transaction -> isLateNight(transaction.getTransactionTime()))
                .count();

        return round((double) lateNightExpenses / expenseCount);
    }

    private boolean isLateNight(LocalTime time) {
        if (time == null) {
            return false;
        }

        int hour = time.getHour();
        return hour >= 23 || hour <= 5;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}