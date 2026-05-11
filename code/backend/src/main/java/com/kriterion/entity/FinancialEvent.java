package com.kriterion.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialEvent extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String type; // e.g., SHOPPING_BURST, SUBSCRIPTION_DETECTED, SALARY_CREDIT, SPENDING_STREAK

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String severity; // INFO, WARNING, SUCCESS

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON for additional info (e.g., related transaction IDs)
}
