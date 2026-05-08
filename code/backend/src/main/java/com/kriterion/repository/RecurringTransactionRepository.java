package com.kriterion.repository;

import com.kriterion.entity.RecurringTransaction;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {
    List<RecurringTransaction> findAllByUserId(Long userId);
    List<RecurringTransaction> findAllByIsActiveTrueAndNextRunDateBefore(LocalDate date);
}
