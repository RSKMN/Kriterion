package com.kriterion.service.intelligence;

import com.kriterion.entity.MerchantMapping;
import com.kriterion.entity.UserCategorizationPreference;
import com.kriterion.repository.MerchantMappingRepository;
import com.kriterion.repository.UserCategorizationPreferenceRepository;
import com.kriterion.util.MerchantNormalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategorizationLearningService {

    private final MerchantMappingRepository mappingRepository;
    private final UserCategorizationPreferenceRepository preferenceRepository;

    /**
     * Learns or updates a mapping between a merchant and a category for a user.
     * Triggered when a user creates or updates a transaction with a specific category.
     */
    @Transactional
    public void learn(Long userId, String rawMerchant, Long categoryId) {
        String normalized = MerchantNormalizer.normalize(rawMerchant);
        if (normalized.isEmpty() || categoryId == null) {
            return;
        }

        // Check if learning is enabled for this user
        if (!isLearningEnabled(userId)) {
            return;
        }

        mappingRepository.findByUserIdAndMerchantName(userId, normalized)
                .ifPresentOrElse(
                    mapping -> {
                        if (mapping.getCategoryId().equals(categoryId)) {
                            // Same category, increment usage/confidence
                            mapping.setUsageCount(mapping.getUsageCount() + 1);
                            mapping.setConfidenceScore(Math.min(1.0, mapping.getConfidenceScore() + 0.05));
                        } else {
                            // Category changed! Pivot the mapping
                            log.info("User {} changed category for merchant '{}' from {} to {}", 
                                    userId, normalized, mapping.getCategoryId(), categoryId);
                            mapping.setCategoryId(categoryId);
                            mapping.setUsageCount(1);
                            mapping.setConfidenceScore(0.8); // High confidence on explicit correction
                        }
                        mapping.setLastUsedAt(LocalDateTime.now());
                        mappingRepository.save(mapping);
                    },
                    () -> {
                        // New mapping
                        MerchantMapping mapping = MerchantMapping.builder()
                                .userId(userId)
                                .merchantName(normalized)
                                .categoryId(categoryId)
                                .usageCount(1)
                                .confidenceScore(0.7) // Initial confidence
                                .lastUsedAt(LocalDateTime.now())
                                .build();
                        mappingRepository.save(mapping);
                    }
                );
    }

    /**
     * Attempts to find a learned mapping for a merchant.
     */
    public Optional<MerchantMapping> findLearnedMapping(Long userId, String rawMerchant) {
        String normalized = MerchantNormalizer.normalize(rawMerchant);
        if (normalized.isEmpty()) {
            return Optional.empty();
        }

        return mappingRepository.findByUserIdAndMerchantName(userId, normalized)
                .filter(m -> m.getConfidenceScore() >= getConfidenceThreshold(userId));
    }

    private boolean isLearningEnabled(Long userId) {
        return preferenceRepository.findByUserId(userId)
                .map(UserCategorizationPreference::getLearningEnabled)
                .orElse(true);
    }

    private double getConfidenceThreshold(Long userId) {
        return preferenceRepository.findByUserId(userId)
                .map(UserCategorizationPreference::getMinConfidenceThreshold)
                .orElse(0.7);
    }
}
