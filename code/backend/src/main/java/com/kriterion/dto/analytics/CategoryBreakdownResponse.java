package com.kriterion.dto.analytics;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryBreakdownResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalExpense;
    private List<CategorySpendingResponse> categories;
}