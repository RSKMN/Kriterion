package com.kriterion.event;

import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class SuspiciousSpendingEvent extends KriterionEvent {
    private final String description;
    private final BigDecimal amount;
    private final String reason;

    public SuspiciousSpendingEvent(Object source, Long userId, String description, BigDecimal amount, String reason) {
        super(source, userId);
        this.description = description;
        this.amount = amount;
        this.reason = reason;
    }
}
