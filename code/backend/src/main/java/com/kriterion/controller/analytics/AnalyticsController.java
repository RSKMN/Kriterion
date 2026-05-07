package com.kriterion.controller.analytics;

import com.kriterion.analytics.AnalyticsService;
import com.kriterion.dto.analytics.CategoryBreakdownResponse;
import com.kriterion.dto.analytics.DashboardSummaryResponse;
import com.kriterion.dto.analytics.MonthlyTrendsResponse;
import com.kriterion.dto.analytics.TopCategoriesResponse;
import com.kriterion.dto.analytics.WeeklyInsightsResponse;
import com.kriterion.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard-summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getDashboardSummary() {
        DashboardSummaryResponse summary = analyticsService.getDashboardSummary();
        return ResponseEntity.ok(ApiResponse.success("Dashboard summary retrieved successfully", summary));
    }

    @GetMapping("/monthly-trends")
    public ResponseEntity<ApiResponse<MonthlyTrendsResponse>> getMonthlyTrends() {
        MonthlyTrendsResponse response = analyticsService.getMonthlyTrends();
        return ResponseEntity.ok(ApiResponse.success("Monthly trends retrieved successfully", response));
    }

    @GetMapping("/category-breakdown")
    public ResponseEntity<ApiResponse<CategoryBreakdownResponse>> getCategoryBreakdown() {
        CategoryBreakdownResponse response = analyticsService.getCategoryBreakdown();
        return ResponseEntity.ok(ApiResponse.success("Category breakdown retrieved successfully", response));
    }

    @GetMapping("/top-categories")
    public ResponseEntity<ApiResponse<TopCategoriesResponse>> getTopCategories() {
        TopCategoriesResponse response = analyticsService.getTopCategories();
        return ResponseEntity.ok(ApiResponse.success("Top categories retrieved successfully", response));
    }

    @GetMapping("/weekly-insights")
    public ResponseEntity<ApiResponse<WeeklyInsightsResponse>> getWeeklyInsights() {
        WeeklyInsightsResponse response = analyticsService.getWeeklyInsights();
        return ResponseEntity.ok(ApiResponse.success("Weekly insights retrieved successfully", response));
    }
}
