package com.kriterion.behaviorlite.service;

import com.kriterion.behaviorlite.dto.BurstWindowResponse;
import com.kriterion.entity.Transaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SpendingBurstDetectorLite {

    private static final long BURST_WINDOW_HOURS = 3;
    private static final int MIN_TRANSACTIONS = 4;

    public List<BurstWindowResponse> detectBursts(List<Transaction> transactions) {
        if (transactions == null || transactions.size() < MIN_TRANSACTIONS) {
            return List.of();
        }

        List<Transaction> ordered = transactions.stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate)
                        .thenComparing(transaction -> transaction.getTransactionTime() == null ? LocalTime.MIDNIGHT : transaction.getTransactionTime()))
                .toList();

        List<BurstWindowResponse> bursts = new ArrayList<>();
        int index = 0;

        while (index < ordered.size()) {
            Transaction start = ordered.get(index);
            LocalDateTime startAt = toDateTime(start);
            LocalDateTime endAt = startAt.plusHours(BURST_WINDOW_HOURS);

            int count = 0;
            double totalAmount = 0.0;
            int cursor = index;

            while (cursor < ordered.size() && !toDateTime(ordered.get(cursor)).isAfter(endAt)) {
                count++;
                totalAmount += safeAmount(ordered.get(cursor).getAmount());
                cursor++;
            }

            if (count >= MIN_TRANSACTIONS) {
                bursts.add(BurstWindowResponse.builder()
                        .label(startAt.toLocalDate() + " " + startAt.toLocalTime())
                        .startAt(startAt)
                        .endAt(endAt)
                        .transactionCount(count)
                        .totalAmount(round(totalAmount))
                        .build());
                index = cursor;
                continue;
            }

            index++;
        }

        return bursts;
    }

    private LocalDateTime toDateTime(Transaction transaction) {
        LocalTime time = transaction.getTransactionTime() == null ? LocalTime.MIDNIGHT : transaction.getTransactionTime();
        return LocalDateTime.of(transaction.getTransactionDate(), time);
    }

    private double safeAmount(BigDecimal amount) {
        return amount == null ? 0.0 : amount.doubleValue();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}