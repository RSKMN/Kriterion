package com.kriterion.analytics.behavioral;

import com.kriterion.analytics.behavioral.util.InsightPriorityCalculator;
import com.kriterion.analytics.behavioral.util.InsightTemplateSystem;
import com.kriterion.dto.analytics.behavioral.CognitiveLoadResponse;
import com.kriterion.dto.analytics.behavioral.FinancialReflection;
import com.kriterion.entity.BehavioralMetrics;
import com.kriterion.entity.BehavioralPattern;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InsightGenerationEngine {

    private final InsightPriorityCalculator priorityCalculator;
    private final InsightTemplateSystem templateSystem;

    public List<FinancialReflection> generateInsights(
            BehavioralMetrics metrics, 
            List<BehavioralPattern> patterns, 
            CognitiveLoadResponse cognitiveLoad) {
        
        List<FinancialReflection> insights = new ArrayList<>();
        
        if (metrics == null) return insights;
        List<BehavioralPattern> safePatterns = patterns == null ? List.of() : patterns;
        double fragmentation = metrics.getAverageTransactionFragmentation() != null ? metrics.getAverageTransactionFragmentation() : 0.0;
        double recurringPressure = metrics.getRecurringObligationPressure() != null ? metrics.getRecurringObligationPressure() : 0.0;
        double volatility = metrics.getSpendingVolatility() != null ? metrics.getSpendingVolatility() : 0.0;
        double rhythmConsistency = metrics.getTransactionRhythmConsistency() != null ? metrics.getTransactionRhythmConsistency() : 0.0;
        double decisionDensity = metrics.getDecisionDensity() != null ? metrics.getDecisionDensity() : 0.0;

        // 1. Fragmentation Insight
        if (fragmentation > 2.5) {
            insights.add(createInsight(
                InsightTemplateSystem.InsightTemplate.FRAGMENTATION,
                fragmentation / 5.0,
                List.of("High micro-transaction frequency", "Category-level splitting detected"),
                safePatterns,
                "Last 30 days"
            ));
        }

        // 2. Spending Structure
        if (cognitiveLoad != null && cognitiveLoad.getSpendingStructureScore() < 0.5) {
            insights.add(createInsight(
                InsightTemplateSystem.InsightTemplate.SPENDING_STRUCTURE,
                1.0 - cognitiveLoad.getSpendingStructureScore(),
                List.of("Low transaction consistency", "High decision density"),
                patterns,
                "Last 14 days"
            ));
        }

        // 3. Subscription Pressure
        if (recurringPressure > 0.4) {
            insights.add(createInsight(
                InsightTemplateSystem.InsightTemplate.SUBSCRIPTION_PRESSURE,
                recurringPressure,
                List.of("Fixed costs exceeding 40% of income", "Automated debit density high"),
                safePatterns,
                "Current month"
            ));
        }

        // 4. Volatility
        if (volatility > 1.2) {
            insights.add(createInsight(
                InsightTemplateSystem.InsightTemplate.VOLATILITY,
                Math.min(1.0, volatility / 2.0),
                List.of("High variance in discretionary categories", "Unpredictable expense spikes"),
                safePatterns,
                "Last 30 days"
            ));
        }

        // 5. Unstable Rhythm
        if (rhythmConsistency < 0.2) {
            insights.add(createInsight(
                InsightTemplateSystem.InsightTemplate.UNSTABLE_RHYTHM,
                1.0 - rhythmConsistency,
                List.of("Non-standard transaction timings", "Temporal entropy increased"),
                safePatterns,
                "Last 30 days"
            ));
        }

        // 6. Decision Density
        if (decisionDensity > 4.0) {
            insights.add(createInsight(
                InsightTemplateSystem.InsightTemplate.DECISION_DENSITY,
                Math.min(1.0, decisionDensity / 10.0),
                List.of("Multiple transactions per hour detected", "Short-window spending bursts"),
                safePatterns,
                "Last 7 days"
            ));
        }

        return priorityCalculator.prioritize(insights);
    }

    private FinancialReflection createInsight(
            InsightTemplateSystem.InsightTemplate template, 
            double confidence, 
            List<String> signals,
            List<BehavioralPattern> patterns,
            String timeRange) {
            
        List<String> matchingPatterns = patterns.stream()
            .map(BehavioralPattern::getPatternName)
            .filter(name -> isRelated(name, template))
            .collect(Collectors.toList());

        return FinancialReflection.builder()
            .title(template.getTitle())
            .description(template.getDescription())
            .confidenceScore(Math.min(1.0, confidence))
            .supportingSignals(signals)
            .detectedPatterns(matchingPatterns)
            .timeRange(timeRange)
            .priority(priorityCalculator.calculatePriority(template.name(), confidence))
            .build();
    }

    private boolean isRelated(String patternName, InsightTemplateSystem.InsightTemplate template) {
        // Simple mapping logic
        switch (template) {
            case FRAGMENTATION: return patternName.contains("FRAGMENTATION");
            case SPENDING_STRUCTURE: return patternName.contains("DECISION") || patternName.contains("BURST");
            case SUBSCRIPTION_PRESSURE: return patternName.contains("SUBSCRIPTION") || patternName.contains("OBLIGATION");
            case VOLATILITY: return patternName.contains("SPIKE") || patternName.contains("BURST");
            case UNSTABLE_RHYTHM: return patternName.contains("NIGHT");
            case DECISION_DENSITY: return patternName.contains("BURST") || patternName.contains("OVERLOAD");
            default: return false;
        }
    }
}
