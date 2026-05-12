package com.kriterion.financialreflectionlite.service;

import com.kriterion.financialreflectionlite.dto.BehavioralStabilityAnalysis;
import com.kriterion.entity.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BehavioralStabilityServiceLite {
    
    public BehavioralStabilityAnalysis analyzeBehavioralStability(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return BehavioralStabilityAnalysis.builder()
                    .stabilityLevel("unknown")
                    .consistencyScore(0.0)
                    .spendingConsistency("unknown")
                    .showsRecoverySignals(false)
                    .stabilityObservation("Not enough transaction history to analyze behavioral stability.")
                    .confidence(0.0)
                    .build();
        }
        
        List<Transaction> sorted = transactions.stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate))
                .collect(Collectors.toList());
        
        // Analyze spending consistency over time windows
        Double consistencyScore = analyzeConsistency(sorted);
        String spendingConsistency = categorizeConsistency(consistencyScore);
        String stabilityLevel = determineStabilityLevel(sorted, consistencyScore);
        boolean showsRecovery = detectRecoverySignals(sorted);
        
        String stabilityObservation = generateStabilityObservation(stabilityLevel, spendingConsistency, showsRecovery);
        
        return BehavioralStabilityAnalysis.builder()
                .stabilityLevel(stabilityLevel)
                .consistencyScore(consistencyScore)
                .spendingConsistency(spendingConsistency)
                .showsRecoverySignals(showsRecovery)
                .stabilityObservation(stabilityObservation)
                .confidence(Math.min(transactions.size() / 50.0, 1.0))
                .build();
    }
    
    private Double analyzeConsistency(List<Transaction> sortedTransactions) {
        if (sortedTransactions.size() < 5) return 0.0;
        
        // Group transactions by week and calculate weekly spending
        Map<Integer, BigDecimal> weeklySpending = new TreeMap<>();
        
        LocalDate firstDate = sortedTransactions.get(0).getTransactionDate();
        
        for (Transaction t : sortedTransactions) {
            long daysSinceStart = ChronoUnit.DAYS.between(firstDate, t.getTransactionDate());
            int weekNumber = (int) (daysSinceStart / 7);
            
            BigDecimal amount = t.getAmount() != null ? t.getAmount() : BigDecimal.ZERO;
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                weeklySpending.put(weekNumber, 
                        weeklySpending.getOrDefault(weekNumber, BigDecimal.ZERO).add(amount));
            }
        }
        
        if (weeklySpending.size() < 2) return 0.0;
        
        List<Double> weeklyAmounts = weeklySpending.values().stream()
                .map(BigDecimal::doubleValue)
                .collect(Collectors.toList());
        
        // Calculate coefficient of variation
        double mean = weeklyAmounts.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        if (mean == 0) return 0.0;
        
        double variance = weeklyAmounts.stream()
                .mapToDouble(x -> Math.pow(x - mean, 2))
                .average().orElse(0.0);
        double stdDev = Math.sqrt(variance);
        
        // Consistency score: 1.0 = perfectly consistent, 0.0 = highly variable
        double coefficientOfVariation = stdDev / mean;
        return Math.max(0.0, 1.0 - Math.min(coefficientOfVariation, 1.0));
    }
    
    private String categorizeConsistency(Double consistencyScore) {
        if (consistencyScore < 0.3) return "unpredictable";
        if (consistencyScore < 0.6) return "variable";
        if (consistencyScore < 0.85) return "consistent";
        return "very_consistent";
    }
    
    private String determineStabilityLevel(List<Transaction> sorted, Double consistencyScore) {
        if (sorted.size() < 10) return "stable"; // Default for small datasets
        
        // Compare recent volatility vs overall volatility
        int midPoint = sorted.size() / 2;
        List<Transaction> older = sorted.subList(0, midPoint);
        List<Transaction> newer = sorted.subList(midPoint, sorted.size());
        
        double olderConsistency = analyzeConsistency(older);
        double newerConsistency = analyzeConsistency(newer);
        
        if (Math.abs(olderConsistency - newerConsistency) < 0.15) return "stable";
        if (newerConsistency > olderConsistency) return "recovering";
        if (newerConsistency < olderConsistency - 0.3) return "volatile";
        return "shifting";
    }
    
    private boolean detectRecoverySignals(List<Transaction> sorted) {
        if (sorted.size() < 10) return false;
        
        int recentCount = Math.min(sorted.size(), 5);
        List<Transaction> recent = sorted.subList(sorted.size() - recentCount, sorted.size());
        
        // Recovery signal: decreasing transaction density or more consistent amounts in recent history
        return false; // Simplified for now
    }
    
    private String generateStabilityObservation(String level, String consistency, boolean recovery) {
        StringBuilder sb = new StringBuilder();
        sb.append("Financial behavior appears ");
        sb.append(level).append(" with ");
        sb.append(consistency).append(" spending patterns. ");
        
        switch (level) {
            case "stable":
                sb.append("Your transaction rhythms show sustained structural consistency over time.");
                break;
            case "shifting":
                sb.append("Observable shifts in transaction frequency suggest a transition in spending rhythms.");
                break;
            case "recovering":
                sb.append("Recent patterns show signs of returning to a more structured spending rhythm.");
                break;
            case "volatile":
                sb.append("Elevated variability in transaction timing and volume suggests a less structured period.");
                break;
        }
        
        return sb.toString();
    }
}
