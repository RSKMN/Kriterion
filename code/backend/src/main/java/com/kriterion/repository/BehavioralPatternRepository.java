package com.kriterion.repository;

import com.kriterion.entity.BehavioralPattern;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BehavioralPatternRepository extends JpaRepository<BehavioralPattern, Long> {
    List<BehavioralPattern> findByUserIdAndIsActiveTrue(Long userId);
    List<BehavioralPattern> findByUserId(Long userId);
}
