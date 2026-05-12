package com.kriterion.analytics.behavioral.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class InsightTemplateSystem {

    @Getter
    @AllArgsConstructor
    public enum InsightTemplate {
        FRAGMENTATION(
            "Elevated Transaction Fragmentation",
            "Your spending is distributed across many small transactions. This typically increases financial tracking complexity."
        ),
        SPENDING_STRUCTURE(
            "Reduced Spending Structure",
            "A decrease in transaction consistency was detected, suggesting a less structured financial rhythm recently."
        ),
        SUBSCRIPTION_PRESSURE(
            "Recurring Subscription Pressure",
            "A high volume of automated recurring obligations is consuming a significant portion of your financial capacity."
        ),
        VOLATILITY(
            "Increased Discretionary Volatility",
            "Significant variance in non-essential spending amounts indicates a more unpredictable discretionary spending pattern."
        ),
        UNSTABLE_RHYTHM(
            "Unstable Spending Rhythm",
            "The timing of your transactions has become less predictable across the day and week."
        ),
        DECISION_DENSITY(
            "High Financial Decision Density",
            "A high frequency of transactions in short periods suggests elevated financial decision pressure."
        );

        private final String title;
        private final String description;
    }
}
