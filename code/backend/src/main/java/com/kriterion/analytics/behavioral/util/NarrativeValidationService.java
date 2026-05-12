package com.kriterion.analytics.behavioral.util;

import com.kriterion.dto.ai.NarrativeResponse;
import org.springframework.stereotype.Service;

@Service
public class NarrativeValidationService {

    public boolean isValid(NarrativeResponse response) {
        return response != null
                && response.getNarrative() != null
                && !response.getNarrative().isBlank()
                && response.getSummary() != null
                && !response.getSummary().isBlank()
                && response.getConfidence() != null
                && !response.getConfidence().isBlank();
    }
}