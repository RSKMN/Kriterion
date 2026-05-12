package com.kriterion.analytics.behavioral.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kriterion.dto.ai.NarrativeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SafeNarrativeParser {

    private final ObjectMapper objectMapper;

    public NarrativeResponse parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }

        try {
            return objectMapper.readValue(raw, NarrativeResponse.class);
        } catch (Exception ignored) {
            return null;
        }
    }
}