package com.kriterion.repository.projection;

import java.math.BigDecimal;

public interface OverallTotalsProjection {
    BigDecimal getTotalIncome();

    BigDecimal getTotalExpense();
}