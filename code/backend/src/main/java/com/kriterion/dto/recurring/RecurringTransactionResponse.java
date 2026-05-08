package com.kriterion.dto.recurring;

import com.kriterion.entity.enums.PaymentMethod;
import com.kriterion.entity.enums.RecurrenceType;
import com.kriterion.entity.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecurringTransactionResponse {
    private Long id;
    private String title;
    private BigDecimal amount;
    private Long categoryId;
    private String categoryName;
    private TransactionType type;
    private RecurrenceType recurrenceType;
    private PaymentMethod paymentMethod;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate nextRunDate;
    private Boolean isActive;
}
