package com.kriterion.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DailyInsightProjection {
    LocalDate getInsightDate();

    BigDecimal getTotalIncome();

    BigDecimal getTotalExpense();
}