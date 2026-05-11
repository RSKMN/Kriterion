package com.kriterion.dto.ai;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class PredictionRequest {
    private String userId;
    private String categoryId;
    private BigDecimal currentMonthSpending;
    private Map<String, BigDecimal> historicalSpending;
    private int forecastPeriodMonths;
}
