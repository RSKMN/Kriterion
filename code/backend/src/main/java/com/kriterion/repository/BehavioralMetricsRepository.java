package com.kriterion.repository;

import com.kriterion.entity.BehavioralMetrics;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BehavioralMetricsRepository extends JpaRepository<BehavioralMetrics, Long> {
    Optional<BehavioralMetrics> findByUserId(Long userId);
}
