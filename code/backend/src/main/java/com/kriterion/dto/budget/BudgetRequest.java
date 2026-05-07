package com.kriterion.dto.budget;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class BudgetRequest {
    private Long categoryId;

    @NotNull
    @PositiveOrZero
    private BigDecimal monthlyLimit;

    @NotNull
    private Integer month;

    @NotNull
    private Integer year;
}
