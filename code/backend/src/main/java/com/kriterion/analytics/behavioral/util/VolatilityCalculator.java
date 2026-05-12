package com.kriterion.analytics.behavioral.util;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class VolatilityCalculator {

    public double calculateStandardDeviation(List<BigDecimal> amounts) {
        if (amounts == null || amounts.size() < 2) return 0.0;
        
        double mean = amounts.stream()
            .mapToDouble(BigDecimal::doubleValue)
            .average()
            .orElse(0.0);
            
        double variance = amounts.stream()
            .mapToDouble(a -> Math.pow(a.doubleValue() - mean, 2))
            .average()
            .orElse(0.0);
            
        return Math.sqrt(variance);
    }
    
    public double calculateCoefficientOfVariation(List<BigDecimal> amounts) {
        if (amounts == null || amounts.isEmpty()) return 0.0;
        double mean = amounts.stream()
            .mapToDouble(BigDecimal::doubleValue)
            .average()
            .orElse(0.0);
        if (mean == 0) return 0.0;
        return calculateStandardDeviation(amounts) / mean;
    }
}
