package com.kriterion.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_categorization_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCategorizationPreference extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "learning_enabled", nullable = false)
    private Boolean learningEnabled = true;

    @Column(name = "auto_apply_learned_rules", nullable = false)
    private Boolean autoApplyLearnedRules = true;

    @Column(name = "min_confidence_threshold", nullable = false)
    private Double minConfidenceThreshold = 0.7;
}
