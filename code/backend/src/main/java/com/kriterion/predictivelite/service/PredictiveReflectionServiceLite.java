package com.kriterion.predictivelite.service;

import com.kriterion.predictivelite.dto.PredictiveInsightResponse;
import com.kriterion.predictivelite.dto.PredictiveForecastResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PredictiveReflectionServiceLite {

    public List<PredictiveInsightResponse> buildReflections(
            PredictiveForecastResponse forecast,
            OverspendingPredictorLite.OverspendingForecast overspending,
            VolatilityTrendAnalyzerLite.VolatilityForecast volatility,
            SubscriptionCreepDetectorLite.SubscriptionCreepForecast subscription,
            SavingsForecastEngineLite.SavingsForecast savings,
            EndOfMonthRiskAnalyzerLite.EndOfMonthRisk endOfMonth) {

        List<PredictiveInsightResponse> insights = new ArrayList<>();

        if (forecast.getFallback() != null && forecast.getFallback()) {
            insights.add(PredictiveInsightResponse.builder()
                    .title("Predictive insights improve with more transaction history")
                    .description("Forecasting requires recurring patterns across multiple spending cycles.")
                    .category("insufficient_data")
                    .weight(0.5)
                    .build());
            return insights;
        }

        // Overspending insights
        if ("high".equals(overspending.risk())) {
            insights.add(PredictiveInsightResponse.builder()
                    .title("High overspending risk detected")
                    .description("Current spending trajectory may exhaust discretionary budget sooner than expected.")
                    .category("overspending")
                    .weight(0.9)
                    .build());
        } else if ("moderate".equals(overspending.risk())) {
            insights.add(PredictiveInsightResponse.builder()
                    .title("Moderate overspending pressure")
                    .description("Spending is tracking above recent baseline. Monitor discretionary expenses closely.")
                    .category("overspending")
                    .weight(0.6)
                    .build());
        }

        // Volatility insights
        if ("increasing".equals(volatility.trend())) {
            insights.add(PredictiveInsightResponse.builder()
                    .title("Spending volatility is increasing")
                    .description("Recent spending patterns show higher variability than previous periods.")
                    .category("volatility")
                    .weight(0.7)
                    .build());
        }

        // Subscription insights
        if ("increasing".equals(subscription.trend())) {
            insights.add(PredictiveInsightResponse.builder()
                    .title("Recurring obligations are growing")
                    .description("Subscription and recurring costs are consuming an increasing share of income.")
                    .category("subscription")
                    .weight(0.7)
                    .build());
        }

        // Savings insights
        if ("declining".equals(savings.trend())) {
            insights.add(PredictiveInsightResponse.builder()
                    .title("Savings trajectory is declining")
                    .description("Net balance is declining over recent periods. Consider reviewing discretionary expenses.")
                    .category("savings")
                    .weight(0.8)
                    .build());
        } else if ("improving".equals(savings.trend())) {
            insights.add(PredictiveInsightResponse.builder()
                    .title("Savings trajectory is improving")
                    .description("Net balance shows positive momentum over recent periods.")
                    .category("savings")
                    .weight(0.6)
                    .build());
        }

        // End of month insights
        if (endOfMonth.likelihood() > 0.5) {
            insights.add(PredictiveInsightResponse.builder()
                    .title("End-of-month spending spike pattern detected")
                    .description("Historical data shows elevated expense activity in the final week of months.")
                    .category("end_of_month")
                    .weight(0.7)
                    .build());
        }

        if (insights.isEmpty()) {
            insights.add(PredictiveInsightResponse.builder()
                    .title("Current financial activity appears stable")
                    .description("Predictive signals show no significant deviations from baseline patterns.")
                    .category("stable")
                    .weight(0.5)
                    .build());
        }

        return insights;
    }
}
