package com.kriterion.event;

import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class BudgetExceededEvent extends KriterionEvent {
    private final String categoryName;
    private final BigDecimal limit;
    private final BigDecimal spent;

    public BudgetExceededEvent(Object source, Long userId, String categoryName, BigDecimal limit, BigDecimal spent) {
        super(source, userId);
        this.categoryName = categoryName;
        this.limit = limit;
        this.spent = spent;
    }
}
