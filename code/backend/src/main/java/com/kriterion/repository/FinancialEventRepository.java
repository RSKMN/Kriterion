package com.kriterion.repository;

import com.kriterion.entity.FinancialEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinancialEventRepository extends JpaRepository<FinancialEvent, Long> {
    List<FinancialEvent> findByUserIdOrderByOccurredAtDesc(Long userId);
    List<FinancialEvent> findByUserIdAndIsReadFalse(Long userId);
}
