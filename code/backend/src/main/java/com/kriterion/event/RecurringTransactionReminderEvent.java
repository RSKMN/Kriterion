package com.kriterion.event;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class RecurringTransactionReminderEvent extends KriterionEvent {
    private final String description;
    private final BigDecimal amount;
    private final LocalDate dueDate;

    public RecurringTransactionReminderEvent(Object source, Long userId, String description, BigDecimal amount, LocalDate dueDate) {
        super(source, userId);
        this.description = description;
        this.amount = amount;
        this.dueDate = dueDate;
    }
}
