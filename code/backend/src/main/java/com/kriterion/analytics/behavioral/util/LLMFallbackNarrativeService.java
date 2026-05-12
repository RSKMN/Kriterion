package com.kriterion.analytics.behavioral.util;

import com.kriterion.dto.ai.NarrativeResponse;
import com.kriterion.dto.analytics.behavioral.CognitiveLoadResponse;
import com.kriterion.entity.BehavioralMetrics;
import com.kriterion.entity.BehavioralPattern;
import java.util.List;

public final class LLMFallbackNarrativeService {

    private LLMFallbackNarrativeService() {
    }

    public static NarrativeResponse createDefaultNarrative() {
        return NarrativeResponse.builder()
                .narrative("Not enough behavioral history yet to synthesize a narrative. As more transactions are recorded, Kriterion will identify rhythms, volatility shifts, and recurring obligations.")
                .summary("Insufficient Data")
                .confidence("LOW")
                .isFallback(true)
                .build();
    }

    public static NarrativeResponse createFallback(
            BehavioralMetrics metrics,
            List<BehavioralPattern> patterns,
            CognitiveLoadResponse cognitiveLoad) {
        if (metrics == null) {
            return createDefaultNarrative();
        }

        int patternCount = patterns == null ? 0 : patterns.size();
        String rhythmText = metrics.getTransactionRhythmConsistency() != null && metrics.getTransactionRhythmConsistency() > 0.5
                ? "a relatively structured rhythm"
                : "an irregular transaction rhythm";
        String loadText = cognitiveLoad != null && cognitiveLoad.getCognitiveLoadScore() != null && cognitiveLoad.getCognitiveLoadScore() > 0.5
                ? "elevated decision pressure"
                : "low or moderate cognitive pressure";

        return NarrativeResponse.builder()
                .narrative("Behavioral analysis currently indicates " + rhythmText + " and " + loadText + ". The system identified " + patternCount + " active pattern(s), but narrative confidence remains limited until more temporal density is available.")
                .summary("Fallback Narrative")
                .confidence("LOW")
                .isFallback(true)
                .build();
    }
}