package com.kriterion.financialreflectionlite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialStructureAnalysis {
    private Double discretionaryRatio;      // percentage of spending that's discretionary
    private Double recurringRatio;          // percentage of spending that's recurring
    private String structureStability;      // stable, shifting, volatile
    private Boolean hasConsistentStructure;
    private String structuralObservation;   // observational insight text
    private SubscriptionPressureAnalysis subscriptionPressure;
    private Double confidence;              // 0.0-1.0 confidence in analysis
}
