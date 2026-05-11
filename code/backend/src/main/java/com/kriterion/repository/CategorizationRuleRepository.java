package com.kriterion.repository;

import com.kriterion.entity.CategorizationRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategorizationRuleRepository extends JpaRepository<CategorizationRule, Long> {

    @Query("SELECT r FROM CategorizationRule r WHERE r.userId IS NULL OR r.userId = :userId ORDER BY r.priority DESC")
    List<CategorizationRule> findAllActiveRules(Long userId);

    List<CategorizationRule> findByUserIdIsNullOrderByPriorityDesc();
}
