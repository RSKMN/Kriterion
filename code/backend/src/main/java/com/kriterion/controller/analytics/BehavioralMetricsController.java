package com.kriterion.controller.analytics;

import com.kriterion.analytics.behavioral.BehavioralMetricsService;
import com.kriterion.dto.analytics.BehavioralMetricsResponse;
import com.kriterion.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/behavioral")
@RequiredArgsConstructor
public class BehavioralMetricsController {

    private final BehavioralMetricsService behavioralMetricsService;

    @GetMapping
    public ResponseEntity<ApiResponse<BehavioralMetricsResponse>> getBehavioralMetrics() {
        BehavioralMetricsResponse metrics = behavioralMetricsService.getBehavioralMetrics();
        return ResponseEntity.ok(ApiResponse.success("Behavioral metrics retrieved successfully", metrics));
    }
}
