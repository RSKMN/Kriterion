package com.kriterion.financialreflectionlite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BehavioralStabilityAnalysis {
    private String stabilityLevel;         // stable, shifting, volatile, recovering
    private Double consistencyScore;       // 0.0-1.0, higher = more consistent
    private String spendingConsistency;    // consistent, variable, unpredictable
    private Boolean showsRecoverySignals;
    private String stabilityObservation;   // observational insight text
    private Double confidence;              // 0.0-1.0 confidence in analysis
}
