package com.kriterion.predictivelite.controller;

import com.kriterion.predictivelite.dto.PredictiveForecastResponse;
import com.kriterion.predictivelite.service.PredictiveAnalyticsLiteService;
import com.kriterion.predictivelite.service.PredictiveAnalyticsLiteSeeder;
import com.kriterion.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/predictive-analytics")
@RequiredArgsConstructor
public class PredictiveLiteController {

    private final PredictiveAnalyticsLiteService forecastService;
    private final PredictiveAnalyticsLiteSeeder seeder;

    @GetMapping("/forecast")
    public ResponseEntity<ApiResponse<PredictiveForecastResponse>> getForecast() {
        PredictiveForecastResponse forecast = forecastService.buildForecast();
        return ResponseEntity.ok(ApiResponse.success("Predictive forecast generated successfully", forecast));
    }

    @PostMapping("/seed")
    public ResponseEntity<ApiResponse<String>> seedDemoData() {
        boolean seeded = seeder.seedIfNeeded();
        String message = seeded ? "Demo data seeded successfully" : "User already has sufficient transaction data";
        return ResponseEntity.ok(ApiResponse.success(message, seeded ? "seeded" : "skipped"));
    }
}
