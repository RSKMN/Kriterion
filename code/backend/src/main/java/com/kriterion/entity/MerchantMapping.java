package com.kriterion.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "merchant_mappings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "merchant_name"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchantMapping extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "merchant_name", nullable = false)
    private String merchantName; // Normalized merchant name

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "usage_count", nullable = false)
    private Integer usageCount = 1;

    @Column(name = "confidence_score", nullable = false)
    private Double confidenceScore = 1.0;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;
}
