package com.kriterion.service.intelligence;

import com.kriterion.entity.Transaction;
import com.kriterion.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContextInferenceEngine {

    private final RecurringPatternDetector recurringDetector;
    private final SpendingBehaviorAnalyzer behaviorAnalyzer;
    private final TransactionRepository transactionRepository;

    /**
     * Infers context for a newly created or updated transaction.
     */
    @Transactional
    public void inferContext(Transaction transaction) {
        log.debug("Inferring context for transaction id={}", transaction.getId());
        
        // 1. Detect recurring patterns (subscriptions/bills)
        recurringDetector.detect(transaction);
        
        // 2. Analyze spending behavior (bursts, impulses)
        behaviorAnalyzer.analyze(transaction);
        
        transactionRepository.save(transaction);
    }
}
