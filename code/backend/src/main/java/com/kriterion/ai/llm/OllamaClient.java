package com.kriterion.ai.llm;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
@RequiredArgsConstructor
public class OllamaClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${kriterion.ai.ollama.url:http://localhost:11434/api/generate}")
    private String ollamaUrl;

    @Value("${kriterion.ai.ollama.model:qwen2.5:32b}")
    private String model;

    public String generate(String prompt) {
        try {
            Map<String, Object> request = Map.of(
                "model", model,
                "prompt", prompt,
                "stream", false,
                "format", "json"
            );

            log.info("Sending request to Ollama model: {}", model);
            Map<String, Object> response = restTemplate.postForObject(ollamaUrl, request, Map.class);
            
            if (response != null && response.containsKey("response")) {
                return (String) response.get("response");
            }
        } catch (Exception e) {
            log.error("Ollama connection failed: {}", e.getMessage());
        }
        return null;
    }
}
