package com.kriterion.analytics.behavioral.util;

import com.kriterion.dto.analytics.behavioral.CognitiveLoadResponse;
import java.util.List;

public final class DefaultCognitiveLoadFactory {

    private DefaultCognitiveLoadFactory() {
    }

    public static CognitiveLoadResponse create() {
        return CognitiveLoadResponse.builder()
                .cognitiveLoadScore(0.0)
                .volatilityScore(0.0)
                .rhythmStabilityScore(0.0)
                .spendingStructureScore(0.0)
                .obligationPressureScore(0.0)
                .factors(List.of())
                .interpretations(List.of("Not enough behavioral history yet to estimate cognitive load."))
                .status("INSUFFICIENT_DATA")
                .build();
    }
}