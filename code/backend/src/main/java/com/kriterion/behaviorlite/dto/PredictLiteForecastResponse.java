package com.kriterion.behaviorlite.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictLiteForecastResponse {
    private String riskLevel;
    private String trendDirection;
    private Double confidence;
    private String narrative;
    private List<String> signals;
    private String dataStatus;
    private Boolean fallback;
}