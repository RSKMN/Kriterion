package com.kriterion.dto.transaction;

import com.kriterion.entity.enums.PaymentMethod;
import com.kriterion.entity.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {
    private Long id;
    private Long userId;
    private Long categoryId;
    private String categoryName;
    private String categoryColor;
    
    private TransactionType type;
    private BigDecimal amount;
    private String title;
    private String description;
    
    private PaymentMethod paymentMethod;
    private LocalDate transactionDate;
    
    private Boolean isRecurring;
    private Long recurringTransactionId;
    private Long receiptId;
    private Boolean aiCategorized;
    
    private String location;
    private String merchantName;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
