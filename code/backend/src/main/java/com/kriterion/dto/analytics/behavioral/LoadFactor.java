package com.kriterion.dto.analytics.behavioral;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoadFactor {
    private String name;
    private Double value; // 0.0 to 1.0
    private String impact; // HIGH, MEDIUM, LOW
}
