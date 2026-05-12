package com.kriterion.analytics.behavioral.util;

import com.kriterion.dto.analytics.behavioral.FinancialReflection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class InsightPriorityCalculator {

    public List<FinancialReflection> prioritize(List<FinancialReflection> insights) {
        return insights.stream()
            .sorted(Comparator.comparing(FinancialReflection::getPriority)
                .thenComparing(Comparator.comparing(FinancialReflection::getConfidenceScore).reversed()))
            .collect(Collectors.toList());
    }

    public int calculatePriority(String templateName, double score) {
        // Lower number = Higher priority
        if (score > 0.8) return 1;
        if (score > 0.6) return 2;
        if (score > 0.4) return 3;
        return 4;
    }
}
