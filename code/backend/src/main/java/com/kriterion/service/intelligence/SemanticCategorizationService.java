package com.kriterion.service.intelligence;

import com.kriterion.entity.Category;
import com.kriterion.repository.CategoryRepository;
import com.kriterion.util.VectorUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class SemanticCategorizationService {

    private final OllamaEmbeddingService embeddingService;
    private final CategoryRepository categoryRepository;

    // Cache category embeddings to avoid re-calculating them for every transaction
    private final Map<Long, List<Double>> categoryEmbeddingCache = new ConcurrentHashMap<>();

    public CategorizationService.CategorizationResult categorizeSemantically(String text) {
        List<Double> textEmbedding = embeddingService.getEmbedding(text);
        
        if (textEmbedding.isEmpty()) {
            return null;
        }

        List<Category> categories = categoryRepository.findAll();
        double maxSimilarity = -1.0;
        Category bestMatch = null;

        for (Category category : categories) {
            List<Double> categoryEmbedding = getCategoryEmbedding(category);
            if (categoryEmbedding.isEmpty()) continue;

            double similarity = VectorUtils.cosineSimilarity(textEmbedding, categoryEmbedding);
            
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                bestMatch = category;
            }
        }

        // Only return if confidence is high enough (e.g., > 0.8)
        if (bestMatch != null && maxSimilarity > 0.75) {
            log.info("Semantic match found for '{}': {} (similarity={})", text, bestMatch.getName(), maxSimilarity);
            return CategorizationService.CategorizationResult.builder()
                    .categoryId(bestMatch.getId())
                    .confidence(maxSimilarity)
                    .matchedPattern("semantic-similarity")
                    .isLearned(false)
                    .isRuleBased(false)
                    .build();
        }

        return null;
    }

    private List<Double> getCategoryEmbedding(Category category) {
        return categoryEmbeddingCache.computeIfAbsent(category.getId(), id -> {
            // Use name + potential keywords for a richer embedding anchor
            String anchorText = category.getName().toLowerCase();
            return embeddingService.getEmbedding(anchorText);
        });
    }

    public void evictCategoryCache() {
        categoryEmbeddingCache.clear();
    }
}
