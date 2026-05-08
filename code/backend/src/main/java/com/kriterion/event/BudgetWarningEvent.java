package com.kriterion.event;

import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class BudgetWarningEvent extends KriterionEvent {
    private final String categoryName;
    private final int threshold;
    private final BigDecimal percentage;

    public BudgetWarningEvent(Object source, Long userId, String categoryName, int threshold, BigDecimal percentage) {
        super(source, userId);
        this.categoryName = categoryName;
        this.threshold = threshold;
        this.percentage = percentage;
    }
}
