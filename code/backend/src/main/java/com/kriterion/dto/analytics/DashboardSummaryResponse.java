package com.kriterion.dto.analytics;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardSummaryResponse {
    private BigDecimal totalBalance;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal monthlyExpense;
    private BigDecimal savings;
    private BigDecimal remainingBalance;
    private List<MonthlySummaryResponse> monthlySummary;
}
