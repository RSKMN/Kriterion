package com.kriterion.dto.analytics.behavioral;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionResponse {
    private String overspendingRisk;
    private String trendDirection;
    private Double confidenceScore;
    private String confidenceState;
    private String forecastNarrative;
    private List<String> supportingSignals;
    private String status;
    private boolean isFallback;
}