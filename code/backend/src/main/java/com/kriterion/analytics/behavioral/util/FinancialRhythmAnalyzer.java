package com.kriterion.analytics.behavioral.util;

import com.kriterion.entity.Transaction;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class FinancialRhythmAnalyzer {

    public double calculateLateNightSpendingRatio(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) return 0.0;
        
        long totalExpenses = transactions.stream()
            .filter(t -> t.getType() == com.kriterion.entity.enums.TransactionType.EXPENSE)
            .count();
            
        if (totalExpenses == 0) return 0.0;
        
        long lateNightExpenses = transactions.stream()
            .filter(t -> t.getType() == com.kriterion.entity.enums.TransactionType.EXPENSE)
            .filter(t -> isLateNight(t.getTransactionTime()))
            .count();
            
        return (double) lateNightExpenses / totalExpenses;
    }
    
    public double calculateRhythmConsistency(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) return 0.0;
        
        // Group by hour of day and calculate variance
        Map<Integer, Long> hourlyCounts = transactions.stream()
            .filter(t -> t.getTransactionTime() != null)
            .collect(Collectors.groupingBy(t -> t.getTransactionTime().getHour(), Collectors.counting()));
            
        if (hourlyCounts.isEmpty()) return 0.0;
        
        double mean = transactions.size() / 24.0;
        double variance = 0.0;
        for (int i = 0; i < 24; i++) {
            long count = hourlyCounts.getOrDefault(i, 0L);
            variance += Math.pow(count - mean, 2);
        }
        
        // Lower variance means more consistent rhythm? Actually, high variance might mean strong "rhythm" (predictable peaks)
        // Let's define consistency as the strength of the primary peak
        double maxPeak = hourlyCounts.values().stream().mapToLong(Long::longValue).max().orElse(0) / (double) transactions.size();
        return maxPeak;
    }

    private boolean isLateNight(LocalTime time) {
        if (time == null) return false;
        int hour = time.getHour();
        return hour >= 23 || hour <= 5;
    }
    
    public Map<Integer, Double> getHourlyPatterns(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) return Map.of();
        Map<Integer, Long> counts = transactions.stream()
            .filter(t -> t.getTransactionTime() != null)
            .collect(Collectors.groupingBy(t -> t.getTransactionTime().getHour(), Collectors.counting()));
            
        long total = transactions.stream().filter(t -> t.getTransactionTime() != null).count();
        if (total == 0) return Map.of();
        
        return counts.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() / (double) total));
    }
}
