package com.kriterion.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NarrativeResponse {
    private String narrative;
    private String summary;
    private String confidence;
    private boolean isFallback;
}
