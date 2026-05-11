package com.kriterion.service.intelligence;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OllamaInferenceService {

    @Value("${app.intelligence.ollama.inference-url:http://localhost:11434/api/generate}")
    private String ollamaUrl;

    @Value("${app.intelligence.ollama.chat-model:qwen2.5}")
    private String modelName;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    /**
     * Sends a prompt to the local LLM and expects a JSON response.
     */
    public <T> T generateJson(String prompt, Class<T> responseType) {
        try {
            log.debug("Sending inference request to Ollama ({})", modelName);
            
            Map<String, Object> request = Map.of(
                "model", modelName,
                "prompt", prompt,
                "stream", false,
                "format", "json",
                "options", Map.of(
                    "temperature", 0.1, // Low temperature for deterministic output
                    "num_predict", 256
                )
            );

            OllamaResponse response = restTemplate.postForObject(ollamaUrl, request, OllamaResponse.class);
            
            if (response != null && response.getResponse() != null) {
                return objectMapper.readValue(response.getResponse(), responseType);
            }
        } catch (Exception e) {
            log.error("Ollama inference failed: {}. Ensure Ollama is running with {} model.", 
                    e.getMessage(), modelName);
        }
        return null;
    }

    @Data
    private static class OllamaResponse {
        private String response;
    }
}
