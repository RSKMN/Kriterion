package com.kriterion.dto.budget;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BudgetStatusResponse {
    private BigDecimal totalBudgetLimit;
    private BigDecimal totalSpent;
    private BigDecimal totalRemaining;
    private BigDecimal totalPercentage;
    private int budgetsCount;
    private int warningBudgetsCount;
    private int exceededBudgetsCount;
    private List<BudgetResponse> budgets;
}
