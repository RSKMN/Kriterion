package com.kriterion.financialreflectionlite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPressureAnalysis {
    private Double pressureScore;           // 0.0-1.0, higher = more pressure
    private String pressureLevel;           // low, moderate, high
    private Integer activeSubscriptionCount;
    private String pressureObservation;     // observational insight text
}
