package com.kriterion.repository;

import com.kriterion.entity.Transaction;
import com.kriterion.repository.projection.CategorySpendingProjection;
import com.kriterion.repository.projection.DailyInsightProjection;
import com.kriterion.repository.projection.MonthlyTrendProjection;
import com.kriterion.repository.projection.OverallTotalsProjection;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

@org.springframework.stereotype.Repository
public interface AnalyticsRepository extends Repository<Transaction, Long> {

    @Query(value = """
            SELECT COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0) AS totalIncome,
                   COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0) AS totalExpense
            FROM transactions t
            WHERE t.user_id = :userId
            """, nativeQuery = true)
    OverallTotalsProjection findOverallTotals(@Param("userId") Long userId);

    @Query(value = """
            SELECT YEAR(t.transaction_date) AS year,
                   MONTH(t.transaction_date) AS month,
                   COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0) AS totalIncome,
                   COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0) AS totalExpense
            FROM transactions t
            WHERE t.user_id = :userId
              AND t.transaction_date BETWEEN :startDate AND :endDate
            GROUP BY YEAR(t.transaction_date), MONTH(t.transaction_date)
            ORDER BY YEAR(t.transaction_date), MONTH(t.transaction_date)
            """, nativeQuery = true)
    List<MonthlyTrendProjection> findMonthlyTrends(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query(value = """
            SELECT c.id AS categoryId,
                   c.name AS categoryName,
                   c.color AS categoryColor,
                   COALESCE(SUM(t.amount), 0) AS totalSpent,
                   COUNT(t.id) AS transactionCount
            FROM transactions t
            INNER JOIN categories c ON c.id = t.category_id
            WHERE t.user_id = :userId
              AND t.type = 'EXPENSE'
              AND t.transaction_date BETWEEN :startDate AND :endDate
            GROUP BY c.id, c.name, c.color
            ORDER BY totalSpent DESC, c.name ASC
            """, nativeQuery = true)
    List<CategorySpendingProjection> findCategorySpendingBreakdown(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query(value = """
            SELECT DATE(t.transaction_date) AS insightDate,
                   COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0) AS totalIncome,
                   COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0) AS totalExpense
            FROM transactions t
            WHERE t.user_id = :userId
              AND t.transaction_date BETWEEN :startDate AND :endDate
            GROUP BY DATE(t.transaction_date)
            ORDER BY DATE(t.transaction_date)
            """, nativeQuery = true)
    List<DailyInsightProjection> findWeeklyInsights(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}