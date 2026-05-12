package com.kriterion.behaviorlite.service;

import com.kriterion.behaviorlite.dto.BehaviorLiteSummaryResponse;
import com.kriterion.behaviorlite.dto.LiteReflectionResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReflectionTemplateServiceLite {

    public List<LiteReflectionResponse> buildReflections(BehaviorLiteSummaryResponse summary) {
        List<LiteReflectionResponse> reflections = new ArrayList<>();

        if (summary == null || Boolean.TRUE.equals(summary.getFallback())) {
            reflections.add(reflection(
                    "Behavioral insights improve as more transaction activity is analyzed.",
                    "The lite module is stable but currently working from low-density data.",
                    "data"));
            reflections.add(reflection(
                    "Not enough financial history yet to estimate spending rhythm.",
                    "A recurring salary cycle, several weeks of expenses, and a few subscription payments will make the signal more informative.",
                    "history"));
            return reflections;
        }

        if (summary.getVolatility() != null && summary.getVolatility() > 1.2) {
            reflections.add(reflection(
                    "Spending rhythm became less structured this week.",
                    "Weekly expense variance is elevated relative to recent periods.",
                    "volatility"));
        }

        if (summary.getLateNightRatio() != null && summary.getLateNightRatio() > 0.15) {
            reflections.add(reflection(
                    "Late-night discretionary spending increased recently.",
                    "A higher share of expenses occurred after typical working hours.",
                    "timing"));
        }

        if (summary.getSubscriptionPressure() != null && summary.getSubscriptionPressure() > 0.35) {
            reflections.add(reflection(
                    "Recurring obligations are consuming a growing share of available balance.",
                    "Recurring payments are becoming a more visible part of the monthly cash flow.",
                    "subscriptions"));
        }

        if (summary.getBursts() != null && !summary.getBursts().isEmpty()) {
            reflections.add(reflection(
                    "Short-window spending bursts are visible in the recent activity.",
                    "Several transactions clustered within brief intervals suggest concentrated spending moments.",
                    "bursts"));
        }

        if (summary.getSavingsTrend() != null && summary.getSavingsTrend() < 0) {
            reflections.add(reflection(
                    "The recent savings trajectory softened.",
                    "The simple balance trend indicates spending is outpacing accumulation in the latest window.",
                    "savings"));
        }

        if (reflections.isEmpty()) {
            reflections.add(reflection(
                    "Behavioral activity is currently balanced.",
                    "The lite module has enough data to render, but no strong deviations were detected.",
                    "stable"));
        }

        return reflections;
    }

    private LiteReflectionResponse reflection(String title, String description, String emphasis) {
        return LiteReflectionResponse.builder()
                .title(title)
                .description(description)
                .emphasis(emphasis)
                .build();
    }
}