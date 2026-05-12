package com.kriterion.analytics.behavioral.util;

import com.kriterion.entity.Transaction;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TransactionDensityAnalyzer {

    public double calculateDecisionDensity(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) return 0.0;
        
        // Group by date and count transactions
        Map<LocalDate, Long> dailyCounts = transactions.stream()
            .collect(Collectors.groupingBy(Transaction::getTransactionDate, Collectors.counting()));
            
        return dailyCounts.values().stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0.0);
    }
    
    public double calculateBurstFrequency(List<Transaction> transactions) {
        if (transactions == null || transactions.size() < 2) return 0.0;
        
        // Define a "burst" as more than 3 transactions in a single day
        Map<LocalDate, Long> dailyCounts = transactions.stream()
            .collect(Collectors.groupingBy(Transaction::getTransactionDate, Collectors.counting()));
            
        long bursts = dailyCounts.values().stream()
            .filter(count -> count > 3)
            .count();
            
        return dailyCounts.isEmpty() ? 0.0 : (double) bursts / dailyCounts.size();
    }

    public double calculateFragmentation(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) return 0.0;
        
        // Fragmentation: high number of small transactions in the same category on the same day
        Map<String, Map<LocalDate, Long>> categoryDailyCounts = transactions.stream()
            .filter(t -> t.getCategory() != null && t.getCategory().getName() != null)
            .collect(Collectors.groupingBy(t -> t.getCategory().getName(),
                     Collectors.groupingBy(Transaction::getTransactionDate, Collectors.counting())));
                     
        return categoryDailyCounts.values().stream()
            .flatMap(m -> m.values().stream())
            .mapToLong(Long::longValue)
            .filter(count -> count > 1)
            .average()
            .orElse(1.0);
    }
}
