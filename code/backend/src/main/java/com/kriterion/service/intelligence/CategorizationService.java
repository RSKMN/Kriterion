package com.kriterion.service.intelligence;

import com.kriterion.entity.CategorizationRule;
import com.kriterion.repository.CategorizationRuleRepository;
import com.kriterion.util.MerchantNormalizer;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategorizationService {

    private final CategorizationRuleRepository ruleRepository;
    private final CategorizationLearningService learningService;
    private final SemanticCategorizationService semanticService;
    private final AiCategorizationService aiService;

    @Data
    @Builder
    public static class CategorizationResult {
        private Long categoryId;
        private Double confidence;
        private String matchedPattern;
        private boolean isRuleBased;
        private boolean isLearned;
        private boolean isSemantic;
        private boolean isLlm;
    }

    /**
     * Attempts to categorize a transaction based on merchant name.
     */
    public CategorizationResult categorize(String rawMerchant, Long userId) {
        String normalized = MerchantNormalizer.normalize(rawMerchant);
        
        if (normalized.isEmpty()) {
            return defaultResult();
        }

        // 1. Check Learning-Based Mappings (Highest Priority)
        var learnedMapping = learningService.findLearnedMapping(userId, rawMerchant);
        if (learnedMapping.isPresent()) {
            log.debug("Learned match found for '{}' for user {}", normalized, userId);
            return CategorizationResult.builder()
                    .categoryId(learnedMapping.get().getCategoryId())
                    .confidence(learnedMapping.get().getConfidenceScore())
                    .matchedPattern(normalized)
                    .isLearned(true)
                    .build();
        }

        // 2. Check Rule-Based Mappings
        List<CategorizationRule> rules = ruleRepository.findAllActiveRules(userId);

        for (CategorizationRule rule : rules) {
            if (isMatch(normalized, rule)) {
                log.debug("Rule match found for '{}' using rule id={}", normalized, rule.getId());
                return CategorizationResult.builder()
                        .categoryId(rule.getCategoryId())
                        .confidence(rule.getConfidence())
                        .matchedPattern(rule.getPattern())
                        .isRuleBased(true)
                        .build();
            }
        }

        // 3. Check Semantic Similarity (Local Embedding AI)
        try {
            CategorizationResult semanticResult = semanticService.categorizeSemantically(normalized);
            if (semanticResult != null) {
                semanticResult.setSemantic(true);
                return semanticResult;
            }
        } catch (Exception e) {
            log.warn("Semantic categorization failed: {}", e.getMessage());
        }

        // 4. Check Local LLM Interpretation (Deep Context AI)
        // This only runs if previous layers fail to find a high-confidence match
        try {
            CategorizationResult aiResult = aiService.categorizeWithLlm(rawMerchant);
            if (aiResult != null && aiResult.getConfidence() > 0.6) {
                return aiResult;
            }
        } catch (Exception e) {
            log.warn("AI categorization failed: {}", e.getMessage());
        }

        return defaultResult();
    }

    private boolean isMatch(String normalized, CategorizationRule rule) {
        String pattern = rule.getPattern().toLowerCase();
        
        return switch (rule.getMatchType()) {
            case EXACT -> normalized.equals(pattern);
            case KEYWORD -> normalized.contains(pattern);
            case REGEX -> {
                try {
                    yield Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(normalized).find();
                } catch (Exception e) {
                    log.error("Invalid regex in rule id={}: {}", rule.getId(), pattern);
                    yield false;
                }
            }
        };
    }

    private CategorizationResult defaultResult() {
        return CategorizationResult.builder()
                .categoryId(1L) // Assuming 1 is "Uncategorized" or "General"
                .confidence(0.0)
                .isRuleBased(false)
                .build();
    }
}
