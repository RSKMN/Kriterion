package com.kriterion.repository.projection;

import java.math.BigDecimal;

public interface MonthlyBalanceProjection {
    Integer getYear();

    Integer getMonth();

    BigDecimal getTotalIncome();

    BigDecimal getTotalExpense();
}