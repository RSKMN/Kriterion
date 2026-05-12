package com.kriterion.financialreflectionlite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReflectionResponse {
    private String category;                // rhythm, density, structure, stability, temporal, pattern
    private String title;                   // short title
    private String observation;             // observational reflection text
    private String tone;                    // analytical, cautionary, positive, neutral
    private Double weight;                  // 0.0-1.0, importance/relevance
    private Boolean isSignificant;         // whether to highlight
}
