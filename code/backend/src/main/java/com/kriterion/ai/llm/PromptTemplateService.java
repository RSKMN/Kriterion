package com.kriterion.ai.llm;

import org.springframework.stereotype.Service;

@Service
public class PromptTemplateService {

    private static final String SYSTEM_INSTRUCTION = """
        You are a financial behavior analyst for Kriterion.
        Your task is to convert structured behavioral signals into readable financial reflections.
        
        RULES:
        - Output ONLY a JSON object with 'narrative', 'summary', and 'confidence' fields.
        - Be concise and financially intelligent.
        - Use non-invasive, research-oriented wording.
        - DO NOT provide psychological diagnosis or therapy advice.
        - DO NOT overhumanize or use emotional language.
        - Focus on behavioral-finance observations (e.g., spending structure, decision density, volatility).
        """;

    public String buildPrompt(String structuredJson) {
        return String.format("%s\n\nINPUT DATA:\n%s\n\nGENERATE REFLECTION:", SYSTEM_INSTRUCTION, structuredJson);
    }
}
