package com.kriterion.dto.budget;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BudgetResponse {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private BigDecimal monthlyLimit;
    private BigDecimal currentSpent;
    private BigDecimal remaining;
    private BigDecimal percentage;
    private Integer month;
    private Integer year;
    private Integer alertThreshold;
    private boolean isWarning;
    private boolean isExceeded;
}
