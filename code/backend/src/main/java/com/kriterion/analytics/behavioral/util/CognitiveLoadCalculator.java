package com.kriterion.analytics.behavioral.util;

import com.kriterion.dto.analytics.behavioral.LoadFactor;
import com.kriterion.entity.BehavioralMetrics;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CognitiveLoadCalculator {

    public double calculateVolatilityScore(BehavioralMetrics metrics) {
        if (metrics == null || metrics.getSpendingVolatility() == null || metrics.getCategoryInstability() == null) return 0.0;
        // Spending volatility + Category instability
        return (metrics.getSpendingVolatility() * 0.6) + (metrics.getCategoryInstability() / 5.0 * 0.4);
    }

    public double calculateRhythmStabilityScore(BehavioralMetrics metrics) {
        if (metrics == null || metrics.getTransactionRhythmConsistency() == null) return 0.0;
        // 1.0 - (1.0 - rhythm consistency)
        return metrics.getTransactionRhythmConsistency();
    }

    public double calculateSpendingStructureScore(BehavioralMetrics metrics) {
        if (metrics == null || metrics.getAverageTransactionFragmentation() == null || metrics.getDecisionDensity() == null) return 0.0;
        // Average of fragmentation and decision density (normalized)
        double fragmentation = Math.min(1.0, metrics.getAverageTransactionFragmentation() / 5.0);
        double density = Math.min(1.0, metrics.getDecisionDensity() / 10.0);
        return 1.0 - ((fragmentation + density) / 2.0); // High score means high structure (low chaos)
    }

    public double calculateObligationPressureScore(BehavioralMetrics metrics, double subscriptionLoad, double budgetPressure) {
        if (metrics == null || metrics.getRecurringObligationPressure() == null) return budgetPressure;
        return (metrics.getRecurringObligationPressure() * 0.4) + (subscriptionLoad * 0.3) + (budgetPressure * 0.3);
    }

    public double aggregateCognitiveLoad(double volatility, double instability, double pressure, double fragmentation) {
        // Weighted average of primary load drivers
        return (volatility * 0.3) + (instability * 0.2) + (pressure * 0.3) + (fragmentation * 0.2);
    }

    public List<LoadFactor> identifyTopFactors(BehavioralMetrics metrics, double budgetPressure, double subscriptionLoad) {
        List<LoadFactor> factors = new ArrayList<>();
        
        if (metrics == null) return factors;

        addFactor(factors, "Spending Volatility", metrics.getSpendingVolatility() != null ? metrics.getSpendingVolatility() : 0.0);
        addFactor(factors, "Decision Density", metrics.getDecisionDensity() != null ? metrics.getDecisionDensity() / 10.0 : 0.0);
        addFactor(factors, "Transaction Fragmentation", metrics.getAverageTransactionFragmentation() != null ? metrics.getAverageTransactionFragmentation() / 5.0 : 0.0);
        addFactor(factors, "Category Instability", metrics.getCategoryInstability() != null ? metrics.getCategoryInstability() / 4.0 : 0.0);
        addFactor(factors, "Budget Pressure", budgetPressure);
        addFactor(factors, "Subscription Load", subscriptionLoad);
        
        return factors;
    }

    private void addFactor(List<LoadFactor> factors, String name, double value) {
        double normalizedValue = Math.min(1.0, value);
        factors.add(LoadFactor.builder()
            .name(name)
            .value(normalizedValue)
            .impact(normalizedValue > 0.7 ? "HIGH" : normalizedValue > 0.4 ? "MEDIUM" : "LOW")
            .build());
    }
}
