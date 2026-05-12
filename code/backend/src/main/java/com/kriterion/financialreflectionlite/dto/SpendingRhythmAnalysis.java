package com.kriterion.financialreflectionlite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpendingRhythmAnalysis {
    private String rhythmPattern;           // structured, irregular, bursty, stable
    private String timeOfDayPreference;    // early, mid, late, night
    private Double irregularityScore;       // 0.0-1.0, higher = more irregular
    private Integer lateNightTransactionRate;  // percentage of transactions after 11 PM
    private Boolean hasNightSpendingPattern;
    private String temporalObservation;     // observational insight text
    private Double confidence;              // 0.0-1.0 confidence in analysis
}
