package com.kriterion.analytics.behavioral.util;

import com.kriterion.dto.analytics.BehavioralMetricsResponse;
import java.time.LocalDateTime;
import java.util.HashMap;

public final class DefaultBehavioralMetricsFactory {

    private DefaultBehavioralMetricsFactory() {
    }

    public static BehavioralMetricsResponse create() {
        return BehavioralMetricsResponse.builder()
                .transactionFrequency(0.0)
                .decisionDensity(0.0)
                .spendingVolatility(0.0)
                .categoryInstability(0.0)
                .discretionarySpendingRatio(0.0)
                .recurringObligationPressure(0.0)
                .lateNightSpendingRatio(0.0)
                .spendingBurstFrequency(0.0)
                .averageTransactionFragmentation(0.0)
                .transactionRhythmConsistency(0.0)
                .lastComputedAt(LocalDateTime.now())
                .hourlyPatterns(new HashMap<>())
                .dailyPatterns(new HashMap<>())
                .weeklyPatterns(new HashMap<>())
                .monthlyPatterns(new HashMap<>())
                .postSalarySpendingShift(0.0)
                .build();
    }
}