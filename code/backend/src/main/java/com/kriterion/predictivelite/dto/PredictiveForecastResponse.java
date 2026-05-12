package com.kriterion.predictivelite.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictiveForecastResponse {
    private String overspendingRisk; // low, moderate, high
    private Integer forecastDaysRemaining; // null if not applicable
    private String volatilityTrend; // stable, increasing, decreasing
    private String subscriptionPressureTrend; // stable, increasing, decreasing
    private String savingsTrend; // improving, stable, declining
    private Double confidence; // 0.0 to 1.0
    private List<PredictiveInsightResponse> insights;
    private String dataStatus; // insufficient_data, partial, ready
    private LocalDateTime generatedAt;
    private Boolean fallback;
}
