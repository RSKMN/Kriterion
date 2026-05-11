package com.kriterion.entity;

import com.kriterion.entity.enums.PaymentMethod;
import com.kriterion.entity.enums.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "receipt_id")
    private Long receiptId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 20)
    private PaymentMethod paymentMethod;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(name = "is_recurring")
    private Boolean isRecurring;

    @Column(name = "recurring_transaction_id")
    private Long recurringTransactionId;

    @Column(name = "ai_categorized")
    private Boolean aiCategorized;

    @Column(length = 255)
    private String location;

    @Column(name = "merchant_name", length = 255)
    private String merchantName;

    @jakarta.persistence.ElementCollection
    @jakarta.persistence.CollectionTable(name = "transaction_tags", joinColumns = @jakarta.persistence.JoinColumn(name = "transaction_id"))
    @Column(name = "tag")
    private java.util.Set<String> tags = new java.util.HashSet<>();
}
