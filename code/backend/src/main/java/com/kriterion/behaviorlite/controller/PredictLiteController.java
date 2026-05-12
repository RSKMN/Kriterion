package com.kriterion.behaviorlite.controller;

import com.kriterion.behaviorlite.dto.BehaviorLiteSummaryResponse;
import com.kriterion.behaviorlite.dto.PredictLiteForecastResponse;
import com.kriterion.behaviorlite.service.BehavioralInsightsLiteService;
import com.kriterion.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/behavior-lite")
@RequiredArgsConstructor
public class PredictLiteController {

    private final BehavioralInsightsLiteService liteService;

    @GetMapping("/prediction")
    public ResponseEntity<ApiResponse<PredictLiteForecastResponse>> getForecast() {
        BehaviorLiteSummaryResponse summary = liteService.buildSummary();
        PredictLiteForecastResponse forecast = liteService.buildForecast(summary);
        return ResponseEntity.ok(ApiResponse.success("Lite forecast retrieved successfully", forecast));
    }
}