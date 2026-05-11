package com.kriterion.service.intelligence;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class OcrCleanupService {

    private final OllamaInferenceService inferenceService;

    @Data
    public static class OcrCleanupResponse {
        private String merchantName;
        private BigDecimal totalAmount;
        private String date;
        private String currency;
        private Double confidence;
    }

    public OcrCleanupResponse cleanupOcrText(String rawOcrText) {
        String prompt = String.format("""
            You are an OCR receipt parser. Below is noisy text from a receipt scan. 
            Extract the merchant name, total amount, and transaction date. 
            If values are missing or unclear, make your best guess based on context.
            
            Raw Text:
            ---
            %s
            ---
            
            Return your answer in strictly JSON format:
            {
              "merchantName": "Clean Name",
              "totalAmount": 123.45,
              "date": "YYYY-MM-DD",
              "currency": "USD/INR/etc",
              "confidence": 0.0 to 1.0
            }
            """, rawOcrText);

        return inferenceService.generateJson(prompt, OcrCleanupResponse.class);
    }
}
