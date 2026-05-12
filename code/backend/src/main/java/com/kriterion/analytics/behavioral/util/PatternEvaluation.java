package com.kriterion.analytics.behavioral.util;

import com.kriterion.dto.analytics.behavioral.BehavioralContext;
import com.kriterion.entity.Transaction;
import com.kriterion.entity.enums.TransactionType;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PatternEvaluation {

    public double evaluateSpendingBurst(BehavioralContext context, double threshold) {
        if (context.getMetrics() == null) return 0.0;
        return context.getMetrics().getSpendingBurstFrequency() > threshold ? 1.0 : 0.0;
    }

    public double evaluatePostSalarySpike(BehavioralContext context, double threshold) {
        // We already have postSalarySpendingShift in our metrics computation logic
        // But let's recalculate or use a threshold
        // For now, let's assume if shift > threshold, we have a spike
        // Note: we might want to store more detailed metrics in BehavioralMetrics entity
        // I'll assume context.getMetrics().getPostSalarySpendingShift() exists (I should add it to the entity if I want it persisted)
        // Since I didn't add it to the entity earlier, I'll calculate it here.
        
        List<Transaction> expenses = context.getTransactions().stream()
            .filter(t -> t.getType() == TransactionType.EXPENSE)
            .collect(Collectors.toList());
            
        if (expenses.isEmpty()) return 0.0;

        double firstWeekAvg = expenses.stream()
                .filter(t -> t.getTransactionDate().getDayOfMonth() <= 7)
                .mapToDouble(t -> t.getAmount().doubleValue())
                .average()
                .orElse(0.0);

        double lastWeekAvg = expenses.stream()
                .filter(t -> t.getTransactionDate().getDayOfMonth() >= 23)
                .mapToDouble(t -> t.getAmount().doubleValue())
                .average()
                .orElse(0.0);

        if (lastWeekAvg == 0) return 0.0;
        double shift = (firstWeekAvg - lastWeekAvg) / lastWeekAvg;
        
        return shift > threshold ? Math.min(1.0, shift / (threshold * 2)) : 0.0;
    }

    public double evaluateLateNightDiscretionary(BehavioralContext context, double threshold) {
        if (context.getMetrics() == null) return 0.0;
        return context.getMetrics().getLateNightSpendingRatio() > threshold ? 1.0 : 0.0;
    }

    public double evaluateSubscriptionAccumulation(BehavioralContext context, double threshold) {
        List<Transaction> recurring = context.getTransactions().stream()
            .filter(t -> t.getIsRecurring() != null && t.getIsRecurring())
            .sorted(Comparator.comparing(Transaction::getTransactionDate))
            .collect(Collectors.toList());
            
        if (recurring.size() < 2) return 0.0;
        
        long earlyCount = recurring.stream()
            .filter(t -> t.getTransactionDate().isBefore(LocalDate.now().minusMonths(3)))
            .count();
        long lateCount = recurring.stream()
            .filter(t -> t.getTransactionDate().isAfter(LocalDate.now().minusMonths(3)))
            .count();
            
        if (earlyCount == 0) return lateCount > 3 ? 0.5 : 0.0;
        double growth = (double) lateCount / earlyCount;
        return growth > threshold ? 1.0 : 0.0;
    }

    public double evaluateSuppressionRebound(BehavioralContext context, double threshold) {
        // Suppression: a period of very low spending (at least 7 days)
        // Rebound: a sharp spike in discretionary spending immediately after
        List<Transaction> expenses = context.getTransactions().stream()
            .filter(t -> t.getType() == TransactionType.EXPENSE)
            .sorted(Comparator.comparing(Transaction::getTransactionDate))
            .collect(Collectors.toList());
            
        if (expenses.size() < 10) return 0.0;
        
        // Simplified detection: check for high variance in daily totals
        Map<LocalDate, Double> dailyTotals = expenses.stream()
            .collect(Collectors.groupingBy(Transaction::getTransactionDate, 
                     Collectors.summingDouble(t -> t.getAmount().doubleValue())));
                     
        // Find if there's a sequence of low days followed by high days
        // This is a complex pattern, for now we'll use a simpler heuristic based on volatility and bursts
        if (context.getMetrics().getSpendingVolatility() > 1.5 && context.getMetrics().getSpendingBurstFrequency() > 0.2) {
            return 0.8;
        }
        return 0.0;
    }
    
    public double evaluateDecisionOverload(BehavioralContext context, double threshold) {
        if (context.getMetrics() == null) return 0.0;
        return context.getMetrics().getDecisionDensity() > threshold ? 1.0 : 0.0;
    }
}
