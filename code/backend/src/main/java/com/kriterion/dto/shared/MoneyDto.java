package com.kriterion.dto.shared;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoneyDto {
    private BigDecimal amount;
    private String currency;

    public static MoneyDto of(BigDecimal amount) {
        return new MoneyDto(amount, "INR"); // Default currency
    }
}
