package com.kriterion.behaviorlite.service;

import com.kriterion.entity.Transaction;
import com.kriterion.entity.enums.TransactionType;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionPressureServiceLite {

    public double calculateSubscriptionPressure(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return 0.0;
        }

        double incomeTotal = transactions.stream()
                .filter(transaction -> transaction.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .filter(amount -> amount != null)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();

        if (incomeTotal <= 0.0) {
            return 0.0;
        }

        double recurringExpenses = transactions.stream()
                .filter(transaction -> transaction.getType() == TransactionType.EXPENSE)
                .filter(transaction -> Boolean.TRUE.equals(transaction.getIsRecurring()))
                .map(Transaction::getAmount)
                .filter(amount -> amount != null)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();

        return round(Math.min(1.0, recurringExpenses / incomeTotal));
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}