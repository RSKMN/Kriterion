package com.kriterion.dto.analytics;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategorySpendingResponse {
    private Long categoryId;
    private String categoryName;
    private String categoryColor;
    private BigDecimal totalSpent;
    private Long transactionCount;
    private BigDecimal percentage;
}