package com.kriterion.ai.llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kriterion.analytics.behavioral.util.LLMFallbackNarrativeService;
import com.kriterion.analytics.behavioral.util.NarrativeValidationService;
import com.kriterion.analytics.behavioral.util.SafeNarrativeParser;
import com.kriterion.dto.ai.NarrativeResponse;
import com.kriterion.dto.analytics.behavioral.CognitiveLoadResponse;
import com.kriterion.entity.BehavioralMetrics;
import com.kriterion.entity.BehavioralPattern;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LlmNarrativeService {

    private final OllamaClient ollamaClient;
    private final PromptTemplateService promptTemplateService;
    private final ObjectMapper objectMapper;
    private final SafeNarrativeParser safeNarrativeParser;
    private final NarrativeValidationService narrativeValidationService;

    public NarrativeResponse generateNarrative(
            BehavioralMetrics metrics, 
            List<BehavioralPattern> patterns, 
            CognitiveLoadResponse cognitiveLoad) {
        
        if (metrics == null) {
            return LLMFallbackNarrativeService.createDefaultNarrative();
        }

        try {
            Map<String, Object> input = Map.of(
                "volatility_score", metrics.getSpendingVolatility() != null ? metrics.getSpendingVolatility() : 0.0,
                "decision_density", metrics.getDecisionDensity() != null ? metrics.getDecisionDensity() : 0.0,
                "patterns", patterns == null ? List.of() : patterns.stream().map(BehavioralPattern::getPatternName).collect(Collectors.toList()),
                "cognitive_load", cognitiveLoad != null ? cognitiveLoad.getCognitiveLoadScore() : 0.0,
                "fragmentation", metrics.getAverageTransactionFragmentation() != null ? metrics.getAverageTransactionFragmentation() : 0.0
            );

            String jsonInput = objectMapper.writeValueAsString(input);
            String prompt = promptTemplateService.buildPrompt(jsonInput);
            
            String llmResponse = ollamaClient.generate(prompt);
            
            if (llmResponse != null) {
                NarrativeResponse parsed = safeNarrativeParser.parse(llmResponse);
                if (narrativeValidationService.isValid(parsed)) {
                    return parsed;
                }

                log.error("Failed to validate LLM response: {}", llmResponse);
            }
        } catch (Exception e) {
            log.error("Narrative generation failed: {}", e.getMessage());
        }

        return generateFallback(metrics, patterns, cognitiveLoad);
    }

    private NarrativeResponse generateFallback(
            BehavioralMetrics metrics, 
            List<BehavioralPattern> patterns, 
            CognitiveLoadResponse cognitiveLoad) {
        return LLMFallbackNarrativeService.createFallback(metrics, patterns, cognitiveLoad);
    }
}
