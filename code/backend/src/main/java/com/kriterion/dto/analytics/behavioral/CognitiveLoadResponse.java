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
public class CognitiveLoadResponse {
    private Double cognitiveLoadScore;
    private Double volatilityScore;
    private Double rhythmStabilityScore;
    private Double spendingStructureScore;
    private Double obligationPressureScore;
    
    private List<LoadFactor> factors;
    private List<String> interpretations;
    private String status; // e.g., "STABLE", "INSUFFICIENT_DATA"
}
