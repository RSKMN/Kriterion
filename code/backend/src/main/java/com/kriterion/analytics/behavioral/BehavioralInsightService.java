package com.kriterion.analytics.behavioral;

import com.kriterion.dto.analytics.behavioral.CognitiveLoadResponse;
import com.kriterion.dto.analytics.behavioral.FinancialReflection;
import com.kriterion.analytics.behavioral.util.BehavioralResponseFactory;
import com.kriterion.entity.BehavioralMetrics;
import com.kriterion.entity.BehavioralPattern;
import com.kriterion.repository.BehavioralMetricsRepository;
import com.kriterion.repository.BehavioralPatternRepository;
import com.kriterion.security.util.AuthenticationUtil;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BehavioralInsightService {

    private final BehavioralMetricsRepository metricsRepository;
    private final BehavioralPatternRepository patternRepository;
    private final CognitiveLoadEngine cognitiveLoadEngine;
    private final InsightGenerationEngine insightGenerationEngine;

    public List<FinancialReflection> getBehavioralInsights() {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        if (userId == null) return BehavioralResponseFactory.createDefaultInsights();

        log.info("Generating behavioral insights for user: {}", userId);

        Optional<BehavioralMetrics> metricsOpt = metricsRepository.findByUserId(userId);
        if (metricsOpt.isEmpty()) {
            return BehavioralResponseFactory.createDefaultInsights();
        }

        List<BehavioralPattern> activePatterns = patternRepository.findByUserIdAndIsActiveTrue(userId);
        CognitiveLoadResponse cognitiveLoad = cognitiveLoadEngine.estimateCognitiveLoad();

        List<FinancialReflection> insights = insightGenerationEngine.generateInsights(metricsOpt.get(), activePatterns, cognitiveLoad);
        return insights == null || insights.isEmpty() ? BehavioralResponseFactory.createDefaultInsights() : insights;
    }
}
