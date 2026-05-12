package com.kriterion.controller.analytics;

import com.kriterion.analytics.behavioral.BehavioralInsightService;
import com.kriterion.dto.analytics.behavioral.FinancialReflection;
import com.kriterion.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/insights")
@RequiredArgsConstructor
public class BehavioralInsightController {

    private final BehavioralInsightService insightService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FinancialReflection>>> getInsights() {
        List<FinancialReflection> insights = insightService.getBehavioralInsights();
        return ResponseEntity.ok(ApiResponse.success("Behavioral insights generated successfully", insights));
    }
}
