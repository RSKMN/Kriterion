package com.kriterion.dto.analytics;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BehavioralMetricsResponse {
    private Double transactionFrequency;
    private Double decisionDensity;
    private Double spendingVolatility;
    private Double categoryInstability;
    private Double discretionarySpendingRatio;
    private Double recurringObligationPressure;
    private Double lateNightSpendingRatio;
    private Double spendingBurstFrequency;
    private Double averageTransactionFragmentation;
    private Double transactionRhythmConsistency;
    
    private Map<Integer, Double> hourlyPatterns;
    private Map<String, Double> dailyPatterns;
    private Map<Integer, Double> weeklyPatterns;
    private Map<Integer, Double> monthlyPatterns;
    
    private Double postSalarySpendingShift;
    private LocalDateTime lastComputedAt;
}
