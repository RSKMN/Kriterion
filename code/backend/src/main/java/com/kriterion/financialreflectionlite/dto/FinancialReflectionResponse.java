package com.kriterion.financialreflectionlite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialReflectionResponse {
    private SpendingRhythmAnalysis spendingRhythm;
    private TransactionDensityAnalysis transactionDensity;
    private FinancialStructureAnalysis financialStructure;
    private BehavioralStabilityAnalysis behavioralStability;
    
    @Builder.Default
    private List<ReflectionResponse> reflections = new ArrayList<>();
    
    private String dataStatus;              // insufficient_data, partial, ready
    private String overallStability;        // stable, shifting, unknown
    private Double analysisConfidence;      // 0.0-1.0, overall confidence
    private LocalDateTime generatedAt;
    private Boolean fallback;               // true if using safe defaults
    
    public static FinancialReflectionResponse empty() {
        return FinancialReflectionResponse.builder()
                .spendingRhythm(SpendingRhythmAnalysis.builder()
                        .confidence(0.0)
                        .build())
                .transactionDensity(TransactionDensityAnalysis.builder()
                        .confidence(0.0)
                        .build())
                .financialStructure(FinancialStructureAnalysis.builder()
                        .confidence(0.0)
                        .build())
                .behavioralStability(BehavioralStabilityAnalysis.builder()
                        .confidence(0.0)
                        .build())
                .reflections(new ArrayList<>())
                .dataStatus("insufficient_data")
                .overallStability("unknown")
                .analysisConfidence(0.0)
                .generatedAt(LocalDateTime.now())
                .fallback(true)
                .build();
    }
}
