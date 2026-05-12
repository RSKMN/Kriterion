package com.kriterion.dto.analytics.behavioral;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialReflection {
    private String title;
    private String description;
    private Double confidenceScore;
    private List<String> supportingSignals;
    private List<String> detectedPatterns;
    private String timeRange;
    private Integer priority; // 1 (High) to 5 (Low)
}
