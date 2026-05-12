package com.kriterion.analytics.behavioral.util;

import com.kriterion.dto.analytics.BehavioralMetricsResponse;
import com.kriterion.dto.ai.NarrativeResponse;
import com.kriterion.dto.analytics.behavioral.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Factory for creating safe, structured default responses for behavioral analytics.
 * Ensures that endpoints never return null or unstable contracts.
 */
public class BehavioralResponseFactory {

    public static BehavioralMetricsResponse createDefaultMetrics() {
        return DefaultBehavioralMetricsFactory.create();
    }

    public static CognitiveLoadResponse createDefaultCognitiveLoad() {
        return DefaultCognitiveLoadFactory.create();
    }

    public static NarrativeResponse createDefaultNarrative() {
        return LLMFallbackNarrativeService.createDefaultNarrative();
    }

    public static List<FinancialReflection> createDefaultInsights() {
        return DefaultInsightFactory.create();
    }

    public static PredictionResponse createDefaultPrediction() {
        return DefaultPredictionFactory.create();
    }
}
