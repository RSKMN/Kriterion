package com.kriterion.dto.analytics.behavioral;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BehavioralPatternResponse {
    private Long id;
    private String patternName;
    private Double confidenceScore;
    private LocalDateTime detectedAt;
    private LocalDateTime lastObservedAt;
    private String evidenceMetadata;
    private Boolean isActive;
}
