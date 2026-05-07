package com.kriterion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ai_categorizations")
public class AiCategorization extends BaseEntity {

    @Column(name = "transaction_id", nullable = false)
    private Long transactionId;

    @Column(name = "predicted_category_id", nullable = false)
    private Long predictedCategoryId;

    @Column(name = "confidence_score", precision = 5, scale = 2)
    private BigDecimal confidenceScore;

    @Column(name = "ai_model", length = 100)
    private String aiModel;

    @Column(name = "was_corrected")
    private Boolean wasCorrected;
}
