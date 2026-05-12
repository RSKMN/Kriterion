package com.kriterion.analytics.behavioral.util;

import com.kriterion.dto.analytics.behavioral.FinancialReflection;
import java.util.List;

public final class DefaultInsightFactory {

    private DefaultInsightFactory() {
    }

    public static List<FinancialReflection> create() {
        return List.of(
                FinancialReflection.builder()
                        .title("Insufficient Behavioral History")
                        .description("Behavioral insights improve as more financial activity is analyzed. Record additional transactions to surface patterns, volatility shifts, and recurring pressure signals.")
                        .confidenceScore(0.0)
                        .supportingSignals(List.of("insufficient_transaction_history", "low_temporal_density"))
                        .detectedPatterns(List.of())
                        .timeRange("Last 30 days")
                        .priority(5)
                        .build()
        );
    }
}