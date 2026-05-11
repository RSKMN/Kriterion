package com.kriterion.dto.ai;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class BehavioralInsightDto {
    private String insightType;
    private String message;
    private double significance;
    private List<String> relatedTransactionIds;
}
