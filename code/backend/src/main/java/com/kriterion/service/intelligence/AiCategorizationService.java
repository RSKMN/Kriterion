package com.kriterion.service.intelligence;

import com.kriterion.entity.Category;
import com.kriterion.repository.CategoryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiCategorizationService {

    private final OllamaInferenceService inferenceService;
    private final CategoryRepository categoryRepository;

    @Data
    public static class AiCategorizationResponse {
        private String categoryName;
        private Double confidence;
        private String reason;
    }

    public CategorizationService.CategorizationResult categorizeWithLlm(String merchantText) {
        List<Category> categories = categoryRepository.findAll();
        String categoryList = categories.stream()
                .map(Category::getName)
                .collect(Collectors.joining(", "));

        String prompt = String.format("""
            You are a financial expert. Analyze the following merchant name/description and map it to the most relevant category from the list below.
            
            Merchant: %s
            Allowed Categories: [%s]
            
            Return your answer in strictly JSON format:
            {
              "categoryName": "matching_category_name",
              "confidence": 0.0 to 1.0,
              "reason": "short explanation"
            }
            """, merchantText, categoryList);

        AiCategorizationResponse response = inferenceService.generateJson(prompt, AiCategorizationResponse.class);

        if (response != null && response.getCategoryName() != null) {
            // Find the category entity matching the name returned by AI
            return categories.stream()
                    .filter(c -> c.getName().equalsIgnoreCase(response.getCategoryName()))
                    .findFirst()
                    .map(c -> {
                        log.info("AI Categorized '{}' as '{}' (confidence={}, reason={})", 
                                merchantText, c.getName(), response.getConfidence(), response.getReason());
                        return CategorizationService.CategorizationResult.builder()
                                .categoryId(c.getId())
                                .confidence(response.getConfidence())
                                .matchedPattern("local-llm-interpretation")
                                .isRuleBased(false)
                                .isLearned(false)
                                .isSemantic(false)
                                .isLlm(true)
                                .build();
                    })
                    .orElse(null);
        }

        return null;
    }
}
