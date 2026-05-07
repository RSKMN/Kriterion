package com.kriterion.dto.analytics;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeeklyInsightResponse {
    private LocalDate date;
    private String dayLabel;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal balance;
}