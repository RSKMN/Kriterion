package com.kriterion.controller.ai;

import com.kriterion.ai.llm.LlmNarrativeService;
import com.kriterion.analytics.behavioral.CognitiveLoadEngine;
import com.kriterion.analytics.behavioral.util.BehavioralResponseFactory;
import com.kriterion.dto.ai.NarrativeResponse;
import com.kriterion.dto.analytics.behavioral.CognitiveLoadResponse;
import com.kriterion.entity.BehavioralMetrics;
import com.kriterion.entity.BehavioralPattern;
import com.kriterion.repository.BehavioralMetricsRepository;
import com.kriterion.repository.BehavioralPatternRepository;
import com.kriterion.response.ApiResponse;
import com.kriterion.security.util.AuthenticationUtil;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/narrative")
@RequiredArgsConstructor
public class BehavioralNarrativeController {

    private final LlmNarrativeService narrativeService;
    private final BehavioralMetricsRepository metricsRepository;
    private final BehavioralPatternRepository patternRepository;
    private final CognitiveLoadEngine cognitiveLoadEngine;

    @GetMapping
    public ResponseEntity<ApiResponse<NarrativeResponse>> getNarrative() {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("User not authenticated", BehavioralResponseFactory.createDefaultNarrative()));
        }

        Optional<BehavioralMetrics> metricsOpt = metricsRepository.findByUserId(userId);
        if (metricsOpt.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success("No behavioral data found", BehavioralResponseFactory.createDefaultNarrative()));
        }

        List<BehavioralPattern> activePatterns = patternRepository.findByUserIdAndIsActiveTrue(userId);
        CognitiveLoadResponse cognitiveLoad = cognitiveLoadEngine.estimateCognitiveLoad();

        NarrativeResponse narrative = narrativeService.generateNarrative(metricsOpt.get(), activePatterns, cognitiveLoad);
        
        return ResponseEntity.ok(ApiResponse.success("Narrative generated successfully", narrative));
    }
}
