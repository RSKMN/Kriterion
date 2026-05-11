package com.kriterion.repository;

import com.kriterion.entity.UserCategorizationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCategorizationPreferenceRepository extends JpaRepository<UserCategorizationPreference, Long> {
    Optional<UserCategorizationPreference> findByUserId(Long userId);
}
