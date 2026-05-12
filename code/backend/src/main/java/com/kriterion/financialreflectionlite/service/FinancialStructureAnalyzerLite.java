package com.kriterion.financialreflectionlite.service;

import com.kriterion.financialreflectionlite.dto.FinancialStructureAnalysis;
import com.kriterion.financialreflectionlite.dto.SubscriptionPressureAnalysis;
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
public class FinancialStructureAnalyzerLite {
    
    // Recurring expense keywords
    private static final Set<String> RECURRING_KEYWORDS = Set.of(
            "subscription", "gym", "netflix", "spotify", "cloud", "insurance",
            "rent", "mortgage", "electric", "water", "internet", "phone",
            "streaming", "recurring", "monthly", "auto", "transfer", "prime",
            "youtube", "apple", "google", "microsoft", "adobe", "saas"
    );
    
    public FinancialStructureAnalysis analyzeFinancialStructure(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return FinancialStructureAnalysis.builder()
                    .discretionaryRatio(0.0)
                    .recurringRatio(0.0)
                    .structureStability("unknown")
                    .hasConsistentStructure(false)
                    .structuralObservation("Not enough transaction history to analyze financial structure.")
                    .subscriptionPressure(SubscriptionPressureAnalysis.builder()
                            .pressureScore(0.0)
                            .pressureLevel("unknown")
                            .activeSubscriptionCount(0)
                            .pressureObservation("Insufficient data to analyze subscription pressure.")
                            .build())
                    .confidence(0.0)
                    .build();
        }
        
        // Identify recurring vs discretionary
        BigDecimal totalSpending = BigDecimal.ZERO;
        BigDecimal recurringSpending = BigDecimal.ZERO;
        Set<String> uniqueSubscriptions = new HashSet<>();
        
        for (Transaction t : transactions) {
            if (t.getAmount() != null && t.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                totalSpending = totalSpending.add(t.getAmount());
                
                if (isRecurring(t)) {
                    recurringSpending = recurringSpending.add(t.getAmount());
                    String subName = identifySubscriptionName(t);
                    if (subName != null) {
                        uniqueSubscriptions.add(subName);
                    }
                }
            }
        }
        
        double discretionaryRatio = 0.0;
        double recurringRatio = 0.0;
        
        if (totalSpending.compareTo(BigDecimal.ZERO) > 0) {
            recurringRatio = recurringSpending.doubleValue() / totalSpending.doubleValue();
            discretionaryRatio = 1.0 - recurringRatio;
        }
        
        // Analyze subscription pressure
        SubscriptionPressureAnalysis subPressure = analyzeSubscriptionPressure(
                recurringSpending, totalSpending, uniqueSubscriptions.size()
        );
        
        // Analyze structure stability over time periods
        String stabilityLevel = analyzeStructureStability(transactions);
        boolean hasConsistent = discretionaryRatio >= 0.2 && discretionaryRatio <= 0.8;
        
        String structuralObservation = generateStructuralObservation(
                discretionaryRatio, recurringRatio, stabilityLevel
        );
        
        return FinancialStructureAnalysis.builder()
                .discretionaryRatio(discretionaryRatio)
                .recurringRatio(recurringRatio)
                .structureStability(stabilityLevel)
                .hasConsistentStructure(hasConsistent)
                .structuralObservation(structuralObservation)
                .subscriptionPressure(subPressure)
                .confidence(Math.min(transactions.size() / 50.0, 1.0))
                .build();
    }
    
    private boolean isRecurring(Transaction t) {
        if (t.getCategory() != null && t.getCategory().getName() != null) {
            String categoryName = t.getCategory().getName().toLowerCase();
            if (RECURRING_KEYWORDS.stream().anyMatch(categoryName::contains)) {
                return true;
            }
        }
        
        if (t.getDescription() != null) {
            String desc = t.getDescription().toLowerCase();
            return RECURRING_KEYWORDS.stream().anyMatch(desc::contains);
        }
        
        return false;
    }

    private String identifySubscriptionName(Transaction t) {
        String desc = t.getDescription();
        if (desc == null) return null;
        desc = desc.toLowerCase();
        
        for (String keyword : RECURRING_KEYWORDS) {
            if (desc.contains(keyword)) {
                return keyword;
            }
        }
        return "other_recurring";
    }

    private SubscriptionPressureAnalysis analyzeSubscriptionPressure(
            BigDecimal recurringAmount, BigDecimal totalAmount, int subCount) {
        
        double ratio = totalAmount.compareTo(BigDecimal.ZERO) > 0 ? 
                recurringAmount.doubleValue() / totalAmount.doubleValue() : 0.0;
        
        double pressureScore = Math.min((ratio * 0.7) + (Math.min(subCount, 10) / 10.0 * 0.3), 1.0);
        
        String level = "low";
        if (pressureScore > 0.6) level = "high";
        else if (pressureScore > 0.3) level = "moderate";
        
        String observation = String.format(
                "Recurring obligations represent %.1f%% of your total spending through %d identified services.",
                ratio * 100, subCount
        );
        
        if (level.equals("high")) {
            observation += " This high ratio of fixed commitments reduces overall spending flexibility.";
        } else if (level.equals("low")) {
            observation += " This suggests a highly flexible financial structure with few fixed commitments.";
        }
        
        return SubscriptionPressureAnalysis.builder()
                .pressureScore(pressureScore)
                .pressureLevel(level)
                .activeSubscriptionCount(subCount)
                .pressureObservation(observation)
                .build();
    }
    
    private String analyzeStructureStability(List<Transaction> transactions) {
        if (transactions.size() < 10) return "unknown";
        
        List<Transaction> sorted = transactions.stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate))
                .collect(Collectors.toList());
        
        LocalDate oldestDate = sorted.get(0).getTransactionDate();
        LocalDate newestDate = sorted.get(sorted.size() - 1).getTransactionDate();
        long totalDays = ChronoUnit.DAYS.between(oldestDate, newestDate);
        
        if (totalDays < 14) return "unknown";
        
        // Compare first half vs second half structure
        int midPoint = sorted.size() / 2;
        List<Transaction> firstHalf = sorted.subList(0, midPoint);
        List<Transaction> secondHalf = sorted.subList(midPoint, sorted.size());
        
        double firstHalfRecurringRatio = calculateRecurringRatio(firstHalf);
        double secondHalfRecurringRatio = calculateRecurringRatio(secondHalf);
        
        double diff = Math.abs(firstHalfRecurringRatio - secondHalfRecurringRatio);
        
        if (diff < 0.1) return "stable";
        if (diff < 0.25) return "shifting";
        return "volatile";
    }
    
    private double calculateRecurringRatio(List<Transaction> transactions) {
        if (transactions.isEmpty()) return 0.0;
        
        BigDecimal recurring = transactions.stream()
                .filter(this::isRecurring)
                .map(Transaction::getAmount)
                .filter(Objects::nonNull)
                .filter(a -> a.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal total = transactions.stream()
                .map(Transaction::getAmount)
                .filter(Objects::nonNull)
                .filter(a -> a.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (total.compareTo(BigDecimal.ZERO) == 0) return 0.0;
        return recurring.doubleValue() / total.doubleValue();
    }
    
    private String generateStructuralObservation(double discretionaryRatio, double recurringRatio, String stabilityLevel) {
        StringBuilder sb = new StringBuilder();
        
        if (recurringRatio > 0.5) {
            sb.append("Your financial structure is heavily weighted towards recurring obligations, which may reduce temporal spending flexibility.");
        } else if (discretionaryRatio > 0.7) {
            sb.append("Spending is primarily discretionary, indicating a fluid financial structure with minimal fixed constraints.");
        } else {
            sb.append("Your financial structure shows a balanced distribution between fixed obligations and discretionary activity.");
        }
        
        if (stabilityLevel.equals("stable")) {
            sb.append(" This structure remains consistent across analysis periods.");
        } else if (stabilityLevel.equals("shifting")) {
            sb.append(" Analysis shows moderate structural variation between periods.");
        } else if (stabilityLevel.equals("volatile")) {
            sb.append(" Analysis exhibits significant structural variation between periods.");
        }
        
        return sb.toString();
    }
}
