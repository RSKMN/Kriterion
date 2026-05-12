package com.kriterion.dto.analytics.behavioral;

import com.kriterion.entity.BehavioralMetrics;
import com.kriterion.entity.Transaction;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BehavioralContext {
    private Long userId;
    private BehavioralMetrics metrics;
    private List<Transaction> transactions;
    private List<Transaction> recentTransactions; // e.g. last 30 days
}
