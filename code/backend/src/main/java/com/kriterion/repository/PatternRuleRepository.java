package com.kriterion.repository;

import com.kriterion.entity.PatternRule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatternRuleRepository extends JpaRepository<PatternRule, Long> {
    List<PatternRule> findByIsEnabledTrue();
}
