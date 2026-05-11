package com.kriterion.service.intelligence;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class OllamaEmbeddingService {

    @Value("${app.intelligence.ollama.url:http://localhost:11434/api/embeddings}")
    private String ollamaUrl;

    @Value("${app.intelligence.ollama.model:nomic-embed-text}")
    private String modelName;

    private final RestTemplate restTemplate = new RestTemplate();
    
    // Simple in-memory cache to avoid redundant local inference
    private final Map<String, List<Double>> embeddingCache = new ConcurrentHashMap<>();

    public List<Double> getEmbedding(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        return embeddingCache.computeIfAbsent(text.toLowerCase().trim(), this::fetchFromOllama);
    }

    private List<Double> fetchFromOllama(String text) {
        try {
            log.debug("Generating embedding for text: '{}' using {}", text, modelName);
            
            Map<String, Object> request = Map.of(
                "model", modelName,
                "prompt", text
            );

            OllamaResponse response = restTemplate.postForObject(ollamaUrl, request, OllamaResponse.class);
            
            if (response != null && response.getEmbedding() != null) {
                return response.getEmbedding();
            }
        } catch (Exception e) {
            log.error("Failed to fetch embedding from Ollama: {}. Ensure Ollama is running locally with {} installed.", 
                    e.getMessage(), modelName);
        }
        return Collections.emptyList();
    }

    @Data
    private static class OllamaResponse {
        private List<Double> embedding;
    }
}
