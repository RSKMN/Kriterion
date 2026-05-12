package com.kriterion.analytics.behavioral.util;

import com.kriterion.dto.analytics.behavioral.PredictionResponse;
import java.util.List;

public final class DefaultPredictionFactory {

    private DefaultPredictionFactory() {
    }

    public static PredictionResponse create() {
        return PredictionResponse.builder()
                .overspendingRisk("unknown")
                .trendDirection("unknown")
                .confidenceScore(0.0)
                .confidenceState("insufficient_data")
                .forecastNarrative("Predictive forecasts become more accurate with recurring transaction patterns and denser financial history.")
                .supportingSignals(List.of("insufficient_transaction_history", "no_forecast_basis"))
                .status("INSUFFICIENT_DATA")
                .isFallback(true)
                .build();
    }
}