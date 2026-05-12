package com.kriterion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "behavioral_metrics")
public class BehavioralMetrics extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "transaction_frequency")
    private Double transactionFrequency; // avg transactions per day

    @Column(name = "decision_density")
    private Double decisionDensity; // transactions per active hour

    @Column(name = "spending_volatility")
    private Double spendingVolatility; // std dev of expense amounts

    @Column(name = "category_instability")
    private Double categoryInstability; // entropy of categories

    @Column(name = "discretionary_spending_ratio")
    private Double discretionarySpendingRatio;

    @Column(name = "recurring_obligation_pressure")
    private Double recurringObligationPressure;

    @Column(name = "late_night_spending_ratio")
    private Double lateNightSpendingRatio;

    @Column(name = "spending_burst_frequency")
    private Double spendingBurstFrequency;

    @Column(name = "avg_transaction_fragmentation")
    private Double averageTransactionFragmentation;

    @Column(name = "transaction_rhythm_consistency")
    private Double transactionRhythmConsistency;

    @Column(name = "last_computed_at")
    private LocalDateTime lastComputedAt;
}
