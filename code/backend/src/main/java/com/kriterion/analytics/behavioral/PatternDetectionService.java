package com.kriterion.analytics.behavioral;

import com.kriterion.analytics.behavioral.util.PatternEvaluation;
import com.kriterion.dto.analytics.behavioral.BehavioralContext;
import com.kriterion.entity.BehavioralMetrics;
import com.kriterion.entity.BehavioralPattern;
import com.kriterion.entity.PatternRule;
import com.kriterion.entity.Transaction;
import com.kriterion.repository.BehavioralMetricsRepository;
import com.kriterion.repository.BehavioralPatternRepository;
import com.kriterion.repository.PatternRuleRepository;
import com.kriterion.repository.TransactionRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatternDetectionService {

    private final PatternRuleRepository ruleRepository;
    private final BehavioralPatternRepository patternRepository;
    private final BehavioralMetricsRepository metricsRepository;
    private final TransactionRepository transactionRepository;
    private final com.kriterion.repository.UserRepository userRepository;
    private final PatternEvaluation evaluationUtils;

    public List<BehavioralPattern> getPatternsForUser() {
        Long userId = com.kriterion.security.util.AuthenticationUtil.getAuthenticatedUserIdAsLong();
        if (userId == null) return List.of();
        return patternRepository.findByUserId(userId);
    }

    @Transactional
    public List<BehavioralPattern> detectPatterns(Long userId) {
        log.info("Running pattern detection for user: {}", userId);
        
        Optional<BehavioralMetrics> metricsOpt = metricsRepository.findByUserId(userId);
        if (metricsOpt.isEmpty()) {
            log.warn("No metrics found for user {}, skipping pattern detection", userId);
            return List.of();
        }
        
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        BehavioralContext context = BehavioralContext.builder()
                .userId(userId)
                .metrics(metricsOpt.get())
                .transactions(transactions)
                .build();
                
        List<PatternRule> activeRules = ruleRepository.findByIsEnabledTrue();
        List<BehavioralPattern> detectedPatterns = new ArrayList<>();
        
        for (PatternRule rule : activeRules) {
            double confidence = evaluateRule(rule, context);
            if (confidence > 0.3) { // Threshold for considering a pattern "detected"
                BehavioralPattern pattern = updateOrSavePattern(userId, rule, confidence);
                detectedPatterns.add(pattern);
            } else {
                deactivatePatternIfExisted(userId, rule);
            }
        }
        
        return detectedPatterns;
    }

    private double evaluateRule(PatternRule rule, BehavioralContext context) {
        double rawScore = 0.0;
        double threshold = rule.getThresholdValue() != null ? rule.getThresholdValue() : 0.0;
        double confidenceWeight = rule.getConfidenceWeight() != null ? rule.getConfidenceWeight() : 1.0;
        
        switch (rule.getName()) {
            case "SPENDING_BURST":
                rawScore = evaluationUtils.evaluateSpendingBurst(context, threshold);
                break;
            case "POST_SALARY_SPIKE":
                rawScore = evaluationUtils.evaluatePostSalarySpike(context, threshold);
                break;
            case "LATE_NIGHT_DISCRETIONARY":
                rawScore = evaluationUtils.evaluateLateNightDiscretionary(context, threshold);
                break;
            case "SUBSCRIPTION_ACCUMULATION":
                rawScore = evaluationUtils.evaluateSubscriptionAccumulation(context, threshold);
                break;
            case "SUPPRESSION_REBOUND":
                rawScore = evaluationUtils.evaluateSuppressionRebound(context, threshold);
                break;
            case "DECISION_OVERLOAD":
                rawScore = evaluationUtils.evaluateDecisionOverload(context, threshold);
                break;
            case "RECURRING_OBLIGATION_STRESS":
                if (context.getMetrics() != null) {
                    Double recurringPressure = context.getMetrics().getRecurringObligationPressure();
                    rawScore = recurringPressure != null && recurringPressure > threshold ? 1.0 : 0.0;
                }
                break;
            case "FINANCIAL_FRAGMENTATION":
                if (context.getMetrics() != null) {
                    Double fragmentation = context.getMetrics().getAverageTransactionFragmentation();
                    rawScore = fragmentation != null && fragmentation > threshold ? 1.0 : 0.0;
                }
                break;
            default:
                log.warn("Unknown rule: {}", rule.getName());
        }
        
        return rawScore * confidenceWeight;
    }

    private BehavioralPattern updateOrSavePattern(Long userId, PatternRule rule, double confidence) {
        List<BehavioralPattern> existing = patternRepository.findByUserId(userId);
        Optional<BehavioralPattern> patternOpt = existing.stream()
                .filter(p -> p.getPatternName().equals(rule.getName()))
                .findFirst();
                
        String evidence = String.format("{\"confidence\": %.2f, \"rule\": \"%s\", \"threshold\": %.2f}", 
                confidence, rule.getName(), rule.getThresholdValue());

        BehavioralPattern pattern;
        if (patternOpt.isPresent()) {
            pattern = patternOpt.get();
            pattern.setConfidenceScore(confidence);
            pattern.setLastObservedAt(LocalDateTime.now());
            pattern.setIsActive(true);
            pattern.setEvidenceMetadata(evidence);
        } else {
            com.kriterion.entity.User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            pattern = BehavioralPattern.builder()
                    .user(user)
                    .patternName(rule.getName())
                    .confidenceScore(confidence)
                    .detectedAt(LocalDateTime.now())
                    .lastObservedAt(LocalDateTime.now())
                    .evidenceMetadata(evidence)
                    .isActive(true)
                    .build();
        }
        
        return patternRepository.save(pattern);
    }

    private void deactivatePatternIfExisted(Long userId, PatternRule rule) {
        List<BehavioralPattern> existing = patternRepository.findByUserId(userId);
        existing.stream()
                .filter(p -> p.getPatternName().equals(rule.getName()) && p.getIsActive())
                .forEach(p -> {
                    p.setIsActive(false);
                    patternRepository.save(p);
                });
    }
}
