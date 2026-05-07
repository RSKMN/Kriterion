package com.kriterion.dto.ai;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionCategorizationResponse {
    private String predictedCategory;
    private BigDecimal confidence;
}
