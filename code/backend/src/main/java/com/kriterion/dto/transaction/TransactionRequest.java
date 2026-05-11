package com.kriterion.dto.transaction;

import com.kriterion.entity.enums.PaymentMethod;
import com.kriterion.entity.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class TransactionRequest {
    private Long id; // Optional, for updates
    private String clientUuid; // Required for deduplication
    private Long version; // For conflict resolution

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    private String title;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be strictly positive")
    private java.math.BigDecimal amount;

    @NotNull(message = "Transaction type is required")
    private TransactionType type;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "Transaction date is required")
    private java.time.LocalDate transactionDate;

    private PaymentMethod paymentMethod;

    @Size(max = 100, message = "Merchant name cannot exceed 100 characters")
    private String merchantName;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @Size(max = 100, message = "Location cannot exceed 100 characters")
    private String location;
    
    private Boolean isRecurring = false;
}
