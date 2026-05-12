package com.kriterion.controller.analytics;

import com.kriterion.analytics.behavioral.PredictionService;
import com.kriterion.dto.analytics.behavioral.PredictionResponse;
import com.kriterion.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/predictions")
@RequiredArgsConstructor
public class PredictionController {

    private final PredictionService predictionService;

    @GetMapping
    public ResponseEntity<ApiResponse<PredictionResponse>> getPrediction() {
        PredictionResponse prediction = predictionService.getPrediction();
        return ResponseEntity.ok(ApiResponse.success("Prediction retrieved successfully", prediction));
    }
}