package com.kriterion.behaviorlite.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BehaviorLiteSummaryResponse {
    private Double volatility;
    private Double lateNightRatio;
    private Double subscriptionPressure;
    private Double savingsTrend;
    private String savingsTrendLabel;
    private Map<Integer, Double> hourlyDistribution;
    private List<SeriesPointResponse> weeklySpending;
    private List<SeriesPointResponse> savingsSeries;
    private List<BurstWindowResponse> bursts;
    private List<LiteReflectionResponse> insights;
    private Integer transactionCount;
    private Integer incomeCount;
    private Integer expenseCount;
    private String dataStatus;
    private LocalDateTime generatedAt;
    private Boolean fallback;
}