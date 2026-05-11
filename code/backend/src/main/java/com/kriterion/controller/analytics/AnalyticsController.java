package com.kriterion.controller.analytics;

import com.kriterion.analytics.AnalyticsService;
import com.kriterion.dto.analytics.*;
import com.kriterion.dto.shared.ApiResponse;
import com.kriterion.dto.shared.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Endpoints for financial insights, trends, and summaries.")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Operation(summary = "Get dashboard summary", description = "Returns high-level financial summary for the dashboard (balance, monthly income/expense).")
    @GetMapping("/dashboard-summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getDashboardSummary() {
        DashboardSummaryResponse summary = analyticsService.getDashboardSummary();
        return ResponseEntity.ok(ApiResponse.success("Dashboard summary retrieved successfully", summary));
    }

    @Operation(summary = "Get monthly trends", description = "Returns income vs expense trends over the last 6 months.")
    @GetMapping("/monthly-trends")
    public ResponseEntity<ApiResponse<MonthlyTrendsResponse>> getMonthlyTrends() {
        MonthlyTrendsResponse response = analyticsService.getMonthlyTrends();
        return ResponseEntity.ok(ApiResponse.success("Monthly trends retrieved successfully", response));
    }

    @Operation(summary = "Get analytics history", description = "Returns paginated monthly summaries.")
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<PagedResponse<MonthlySummaryResponse>>> getAnalyticsHistory(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MonthlySummaryResponse> history = analyticsService.getMonthlyHistory(pageable);
        return ResponseEntity.ok(ApiResponse.success("Analytics history retrieved successfully", PagedResponse.of(history)));
    }

    @Operation(summary = "Get category breakdown", description = "Returns expense distribution across all categories.")
    @GetMapping("/category-breakdown")
    public ResponseEntity<ApiResponse<CategoryBreakdownResponse>> getCategoryBreakdown() {
        CategoryBreakdownResponse response = analyticsService.getCategoryBreakdown();
        return ResponseEntity.ok(ApiResponse.success("Category breakdown retrieved successfully", response));
    }

    @Operation(summary = "Get top categories", description = "Returns the top spending categories for the current month.")
    @GetMapping("/top-categories")
    public ResponseEntity<ApiResponse<TopCategoriesResponse>> getTopCategories() {
        TopCategoriesResponse response = analyticsService.getTopCategories();
        return ResponseEntity.ok(ApiResponse.success("Top categories retrieved successfully", response));
    }

    @Operation(summary = "Get weekly insights", description = "Returns spending insights and comparisons for the current week.")
    @GetMapping("/weekly-insights")
    public ResponseEntity<ApiResponse<WeeklyInsightsResponse>> getWeeklyInsights() {
        WeeklyInsightsResponse response = analyticsService.getWeeklyInsights();
        return ResponseEntity.ok(ApiResponse.success("Weekly insights retrieved successfully", response));
    }
}
