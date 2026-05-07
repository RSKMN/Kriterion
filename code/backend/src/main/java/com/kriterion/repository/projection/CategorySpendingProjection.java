package com.kriterion.repository.projection;

import java.math.BigDecimal;

public interface CategorySpendingProjection {
    Long getCategoryId();

    String getCategoryName();

    String getCategoryColor();

    BigDecimal getTotalSpent();

    Long getTransactionCount();
}