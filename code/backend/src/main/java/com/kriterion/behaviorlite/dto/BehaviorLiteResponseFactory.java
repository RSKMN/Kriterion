package com.kriterion.behaviorlite.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public final class BehaviorLiteResponseFactory {

    private BehaviorLiteResponseFactory() {
    }

    public static BehaviorLiteSummaryResponse defaultSummary() {
        return BehaviorLiteSummaryResponse.builder()
                .volatility(0.0)
                .lateNightRatio(0.0)
                .subscriptionPressure(0.0)
                .savingsTrend(0.0)
                .savingsTrendLabel("stable")
                .hourlyDistribution(defaultHourlyDistribution())
                .weeklySpending(List.of())
                .savingsSeries(List.of())
                .bursts(List.of())
                .insights(List.of())
                .transactionCount(0)
                .incomeCount(0)
                .expenseCount(0)
                .dataStatus("insufficient_data")
                .generatedAt(LocalDateTime.now())
                .fallback(true)
                .build();
    }

    public static PredictLiteForecastResponse defaultForecast() {
        return PredictLiteForecastResponse.builder()
                .riskLevel("unknown")
                .trendDirection("stable")
                .confidence(0.0)
                .narrative("Behavioral forecasts become more informative as recurring transactions and salary cycles accumulate.")
                .signals(List.of())
                .dataStatus("insufficient_data")
                .fallback(true)
                .build();
    }

    private static Map<Integer, Double> defaultHourlyDistribution() {
        java.util.LinkedHashMap<Integer, Double> distribution = new java.util.LinkedHashMap<>();
        for (int hour = 0; hour < 24; hour++) {
            distribution.put(hour, 0.0);
        }
        return distribution;
    }
}