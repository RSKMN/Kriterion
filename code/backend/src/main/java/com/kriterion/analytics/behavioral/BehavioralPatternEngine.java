package com.kriterion.analytics.behavioral;

import com.kriterion.dto.analytics.BehavioralMetricsResponse;
import com.kriterion.entity.BehavioralPattern;
import com.kriterion.security.util.AuthenticationUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BehavioralPatternEngine {

    private final BehavioralMetricsService metricsService;
    private final PatternDetectionService detectionService;

    /**
     * Executes the full behavioral analysis pipeline for a user.
     * 1. Compute metrics
     * 2. Detect patterns
     */
    public List<BehavioralPattern> runAnalysis() {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        if (userId == null) return List.of();
        
        log.info("Starting behavioral analysis engine for user: {}", userId);
        
        // Step 1: Ensure metrics are up to date
        metricsService.getBehavioralMetrics();
        
        // Step 2: Detect patterns based on metrics and transactions
        return detectionService.detectPatterns(userId);
    }
    
    public List<BehavioralPattern> getActivePatterns() {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        if (userId == null) return List.of();
        
        // If no patterns exist, run analysis first?
        // For now, just return what's in DB
        return detectionService.detectPatterns(userId);
    }
}
