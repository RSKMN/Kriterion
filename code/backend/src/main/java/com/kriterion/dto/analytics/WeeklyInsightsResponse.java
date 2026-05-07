package com.kriterion.dto.analytics;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeeklyInsightsResponse {
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal balance;
    private List<WeeklyInsightResponse> insights;
}