package com.kriterion.analytics.behavioral.util;

import com.kriterion.entity.Category;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class SpendingPatternUtilities {

    private static final Set<String> ESSENTIAL_CATEGORIES = Set.of(
        "Rent", "Mortgage", "Utilities", "Groceries", "Insurance", "Health", "Education", "Tax", "Transport"
    );

    public boolean isDiscretionary(Category category) {
        if (category == null) return true;
        String name = category.getName();
        return !ESSENTIAL_CATEGORIES.stream().anyMatch(ess -> name.toLowerCase().contains(ess.toLowerCase()));
    }
    
    public double calculateEntropy(java.util.List<String> labels) {
        if (labels == null || labels.isEmpty()) return 0.0;
        java.util.Map<String, Long> counts = labels.stream()
            .collect(java.util.stream.Collectors.groupingBy(java.util.function.Function.identity(), java.util.stream.Collectors.counting()));
        
        double total = labels.size();
        double entropy = 0.0;
        for (long count : counts.values()) {
            double p = count / total;
            entropy -= p * (Math.log(p) / Math.log(2));
        }
        return entropy;
    }
}
