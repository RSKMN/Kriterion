package com.kriterion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Table(name = "pattern_rules")
public class PatternRule extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "logic_definition", columnDefinition = "TEXT")
    private String logicDefinition; // Structured logic or keywords

    @Column(name = "threshold_value")
    private Double thresholdValue;

    @Column(name = "is_enabled")
    private Boolean isEnabled;

    @Column(name = "confidence_weight")
    private Double confidenceWeight;
}
