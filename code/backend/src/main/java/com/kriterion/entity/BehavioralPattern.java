package com.kriterion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "behavioral_patterns")
public class BehavioralPattern extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String patternName;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "detected_at")
    private LocalDateTime detectedAt;

    @Column(name = "last_observed_at")
    private LocalDateTime lastObservedAt;

    @Column(name = "evidence_metadata", columnDefinition = "TEXT")
    private String evidenceMetadata; // JSON string with supporting metrics

    @Column(name = "is_active")
    private Boolean isActive;
}
