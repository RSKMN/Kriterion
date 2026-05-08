package com.kriterion.repository;

import com.kriterion.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    java.util.List<Budget> findByUserIdAndMonthAndYear(Long userId, int month, int year);
    java.util.Optional<Budget> findByUserIdAndCategoryIdAndMonthAndYear(Long userId, Long categoryId, int month, int year);
}
