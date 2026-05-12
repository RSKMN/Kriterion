package com.kriterion.predictivelite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictiveInsightResponse {
    private String title;
    private String description;
    private String category; // overspending, subscription, volatility, savings, end_of_month
    private Double weight; // 0.0 to 1.0, importance
}
