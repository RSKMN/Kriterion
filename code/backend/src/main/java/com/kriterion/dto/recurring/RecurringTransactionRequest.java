package com.kriterion.dto.recurring;

import com.kriterion.entity.enums.RecurrenceType;
import com.kriterion.entity.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class RecurringTransactionRequest {
    @NotBlank
    private String title;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private TransactionType type;

    @NotNull
    private Long categoryId;

    @NotNull
    private RecurrenceType recurrenceType;

    @NotNull
    private LocalDate startDate;
}
