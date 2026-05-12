package com.kriterion.financialreflectionlite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDensityAnalysis {
    private Integer averageTransactionsPerDay;
    private Integer averageTransactionsPerWeek;
    private String densityLevel;           // low, moderate, high, very_high
    private Boolean hasBurstPatterns;      // clustering behavior
    private Double burstFrequency;         // 0.0-1.0, likelihood of burst patterns
    private String densityObservation;     // observational insight text
    private Double confidence;              // 0.0-1.0 confidence in analysis
}
