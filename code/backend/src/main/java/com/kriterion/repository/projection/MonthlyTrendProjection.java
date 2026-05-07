package com.kriterion.repository.projection;

import java.math.BigDecimal;

public interface MonthlyTrendProjection {
    Integer getYear();

    Integer getMonth();

    BigDecimal getTotalIncome();

    BigDecimal getTotalExpense();
}