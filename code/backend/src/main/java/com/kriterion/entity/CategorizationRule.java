package com.kriterion.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categorization_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorizationRule extends BaseEntity {

    @Column(nullable = false)
    private String pattern;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchType matchType;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "user_id")
    private Long userId; // Null for global rules

    @Column(nullable = false)
    private Integer priority;

    @Column(nullable = false)
    private Double confidence;

    public enum MatchType {
        EXACT,
        KEYWORD,
        REGEX
    }
}
