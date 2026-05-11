package com.kriterion.dto.ai;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OcrConfidenceDto {
    private double overallScore;
    private double merchantScore;
    private double amountScore;
    private double dateScore;
}
