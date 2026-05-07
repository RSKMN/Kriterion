package com.kriterion.repository.projection;

import java.math.BigDecimal;

public interface DashboardBalanceProjection {
    BigDecimal getTotalIncome();

    BigDecimal getTotalExpense();
}