package com.kriterion.financialreflectionlite.service;

import com.kriterion.financialreflectionlite.dto.SpendingRhythmAnalysis;
import com.kriterion.entity.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpendingRhythmAnalyzerLite {
    
    public SpendingRhythmAnalysis analyzeSpendingRhythm(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return SpendingRhythmAnalysis.builder()
                    .rhythmPattern("unknown")
                    .timeOfDayPreference("unknown")
                    .irregularityScore(0.0)
                    .lateNightTransactionRate(0)
                    .hasNightSpendingPattern(false)
                    .temporalObservation("Not enough transaction history to analyze spending rhythms.")
                    .confidence(0.0)
                    .build();
        }
        
        List<Transaction> sortedByDate = transactions.stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate))
                .collect(Collectors.toList());
        
        // Analyze time of day preferences
        Map<Integer, Long> hourDistribution = sortedByDate.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionTime() != null ? t.getTransactionTime().getHour() : 12,
                        Collectors.counting()
                ));
        
        int lateNightCount = (int) sortedByDate.stream()
                .filter(t -> {
                    LocalTime time = t.getTransactionTime();
                    return time != null && (time.getHour() >= 23 || time.getHour() < 5);
                })
                .count();
        int lateNightRate = Math.round((lateNightCount * 100.0f) / transactions.size());
        boolean hasNightPattern = lateNightRate > 15;
        
        // Analyze inter-transaction gaps for rhythm regularity
        List<Long> gaps = new ArrayList<>();
        for (int i = 1; i < sortedByDate.size(); i++) {
            LocalDate prev = sortedByDate.get(i - 1).getTransactionDate();
            LocalDate curr = sortedByDate.get(i).getTransactionDate();
            long daysBetween = ChronoUnit.DAYS.between(prev, curr);
            if (daysBetween > 0) {
                gaps.add(daysBetween);
            }
        }
        
        // Calculate irregularity score (coefficient of variation of gaps)
        Double irregularityScore = 0.0;
        String rhythmPattern = "irregular";
        if (!gaps.isEmpty()) {
            double avgGap = gaps.stream().mapToLong(Long::longValue).average().orElse(1.0);
            double variance = gaps.stream()
                    .mapToDouble(g -> Math.pow(g - avgGap, 2))
                    .average().orElse(0.0);
            double stdDev = Math.sqrt(variance);
            irregularityScore = avgGap > 0 ? Math.min(stdDev / avgGap, 1.0) : 0.0;
            
            if (irregularityScore < 0.3) rhythmPattern = "structured";
            else if (irregularityScore < 0.6) rhythmPattern = "stable";
            else if (irregularityScore < 0.8) rhythmPattern = "variable";
            else rhythmPattern = "bursty";
        }
        
        String timeOfDay = determinePrimaryTimeOfDay(hourDistribution);
        String temporalObservation = generateTemporalObservation(rhythmPattern, timeOfDay, hasNightPattern);
        
        return SpendingRhythmAnalysis.builder()
                .rhythmPattern(rhythmPattern)
                .timeOfDayPreference(timeOfDay)
                .irregularityScore(irregularityScore)
                .lateNightTransactionRate(lateNightRate)
                .hasNightSpendingPattern(hasNightPattern)
                .temporalObservation(temporalObservation)
                .confidence(Math.min(transactions.size() / 50.0, 1.0))
                .build();
    }
    
    private String determinePrimaryTimeOfDay(Map<Integer, Long> hourDistribution) {
        if (hourDistribution.isEmpty()) return "unknown";
        
        long earlyCount = hourDistribution.entrySet().stream()
                .filter(e -> e.getKey() >= 6 && e.getKey() < 12)
                .mapToLong(Map.Entry::getValue).sum();
        long midCount = hourDistribution.entrySet().stream()
                .filter(e -> e.getKey() >= 12 && e.getKey() < 18)
                .mapToLong(Map.Entry::getValue).sum();
        long lateCount = hourDistribution.entrySet().stream()
                .filter(e -> e.getKey() >= 18 && e.getKey() < 23)
                .mapToLong(Map.Entry::getValue).sum();
        long nightCount = hourDistribution.entrySet().stream()
                .filter(e -> e.getKey() >= 23 || e.getKey() < 6)
                .mapToLong(Map.Entry::getValue).sum();
        
        long max = Math.max(Math.max(earlyCount, midCount), Math.max(lateCount, nightCount));
        if (max == earlyCount) return "early";
        if (max == midCount) return "mid";
        if (max == lateCount) return "late";
        return "night";
    }
    
    private String generateTemporalObservation(String rhythmPattern, String timeOfDay, boolean hasNightPattern) {
        StringBuilder sb = new StringBuilder();
        
        if (rhythmPattern.equals("structured")) {
            sb.append("Your spending rhythm shows structured regularity, ");
        } else if (rhythmPattern.equals("stable")) {
            sb.append("Your spending pattern is relatively stable, ");
        } else if (rhythmPattern.equals("variable")) {
            sb.append("Your spending rhythm exhibits variability, ");
        } else {
            sb.append("Your spending shows irregular bursts with ");
        }
        
        switch (timeOfDay) {
            case "early": sb.append("concentrated in early morning hours. "); break;
            case "mid": sb.append("concentrated during midday hours. "); break;
            case "late": sb.append("concentrated in evening hours. "); break;
            case "night": sb.append("concentrated in nighttime hours. "); break;
            default: sb.append("distributed across hours. ");
        }
        
        if (hasNightPattern) {
            sb.append("Notable late-night transaction activity detected.");
        }
        
        return sb.toString();
    }
}
