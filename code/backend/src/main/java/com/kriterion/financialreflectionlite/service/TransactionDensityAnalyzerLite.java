package com.kriterion.financialreflectionlite.service;

import com.kriterion.financialreflectionlite.dto.TransactionDensityAnalysis;
import com.kriterion.entity.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionDensityAnalyzerLite {
    
    public TransactionDensityAnalysis analyzeTransactionDensity(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return TransactionDensityAnalysis.builder()
                    .averageTransactionsPerDay(0)
                    .averageTransactionsPerWeek(0)
                    .densityLevel("unknown")
                    .hasBurstPatterns(false)
                    .burstFrequency(0.0)
                    .densityObservation("Not enough transaction history to analyze density patterns.")
                    .confidence(0.0)
                    .build();
        }
        
        List<Transaction> sortedByDate = transactions.stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate))
                .collect(Collectors.toList());
        
        LocalDate oldestDate = sortedByDate.get(0).getTransactionDate();
        LocalDate newestDate = sortedByDate.get(sortedByDate.size() - 1).getTransactionDate();
        long totalDays = Math.max(ChronoUnit.DAYS.between(oldestDate, newestDate), 1);
        
        double avgTransactionsPerDay = transactions.size() / (double) totalDays;
        double avgTransactionsPerWeek = avgTransactionsPerDay * 7;
        
        // Analyze burst patterns
        Map<LocalDate, Integer> dayTransactionCounts = new TreeMap<>();
        for (Transaction t : sortedByDate) {
            LocalDate dateOnly = t.getTransactionDate();
            dayTransactionCounts.put(dateOnly, dayTransactionCounts.getOrDefault(dateOnly, 0) + 1);
        }
        
        List<Integer> counts = new ArrayList<>(dayTransactionCounts.values());
        double avgCount = counts.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        double stdDev = Math.sqrt(counts.stream()
                .mapToDouble(c -> Math.pow(c - avgCount, 2))
                .average().orElse(0.0));
        
        // Burst patterns: high variance in daily counts
        double burstFrequency = 0.0;
        boolean hasBurstPatterns = false;
        if (avgCount > 0) {
            burstFrequency = Math.min(stdDev / avgCount, 1.0);
            hasBurstPatterns = burstFrequency > 0.5;
        }
        
        String densityLevel = calculateDensityLevel(avgTransactionsPerDay);
        String densityObservation = generateDensityObservation(densityLevel, avgTransactionsPerDay, hasBurstPatterns);
        
        return TransactionDensityAnalysis.builder()
                .averageTransactionsPerDay((int) Math.round(avgTransactionsPerDay))
                .averageTransactionsPerWeek((int) Math.round(avgTransactionsPerWeek))
                .densityLevel(densityLevel)
                .hasBurstPatterns(hasBurstPatterns)
                .burstFrequency(burstFrequency)
                .densityObservation(densityObservation)
                .confidence(Math.min(transactions.size() / 50.0, 1.0))
                .build();
    }
    
    private String calculateDensityLevel(double avgPerDay) {
        if (avgPerDay < 0.5) return "low";
        if (avgPerDay < 1.5) return "moderate";
        if (avgPerDay < 3.0) return "high";
        return "very_high";
    }
    
    private String generateDensityObservation(String densityLevel, double avgPerDay, boolean hasBurstPatterns) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Transaction frequency is ");
        switch (densityLevel) {
            case "low": 
                sb.append("relatively low, averaging ");
                break;
            case "moderate":
                sb.append("moderate, averaging ");
                break;
            case "high":
                sb.append("quite high, averaging ");
                break;
            case "very_high":
                sb.append("very high, averaging ");
                break;
        }
        
        sb.append(String.format("%.1f transactions daily. ", avgPerDay));
        
        if (hasBurstPatterns) {
            sb.append("Clustering patterns suggest periodic transaction bursts.");
        } else {
            sb.append("Transactions are fairly distributed throughout the analysis period.");
        }
        
        return sb.toString();
    }
}
