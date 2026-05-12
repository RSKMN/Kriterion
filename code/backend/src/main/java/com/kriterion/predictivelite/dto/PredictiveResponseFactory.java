package com.kriterion.predictivelite.dto;

import java.time.LocalDateTime;
import java.util.List;

public final class PredictiveResponseFactory {

    private PredictiveResponseFactory() {
    }

    public static PredictiveForecastResponse defaultForecast() {
        return PredictiveForecastResponse.builder()
                .overspendingRisk("low")
                .forecastDaysRemaining(null)
                .volatilityTrend("stable")
                .subscriptionPressureTrend("stable")
                .savingsTrend("stable")
                .confidence(0.0)
                .insights(List.of())
                .dataStatus("insufficient_data")
                .generatedAt(LocalDateTime.now())
                .fallback(true)
                .build();
    }
}
