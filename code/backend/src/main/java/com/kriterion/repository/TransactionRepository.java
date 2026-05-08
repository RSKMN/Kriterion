package com.kriterion.repository;

import com.kriterion.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

import com.kriterion.repository.projection.DashboardBalanceProjection;
import com.kriterion.repository.projection.MonthlyBalanceProjection;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    Page<Transaction> findByUserId(Long userId, Pageable pageable);

    @Query("""
             SELECT COALESCE(SUM(CASE WHEN t.type = com.kriterion.entity.enums.TransactionType.INCOME THEN t.amount ELSE NULL END), 0) AS totalIncome,
                     COALESCE(SUM(CASE WHEN t.type = com.kriterion.entity.enums.TransactionType.EXPENSE THEN t.amount ELSE NULL END), 0) AS totalExpense
            FROM Transaction t
            WHERE t.user.id = :userId
            """)
    DashboardBalanceProjection findDashboardBalanceByUserId(@Param("userId") Long userId);

    @Query(value = """
            SELECT YEAR(t.transaction_date) AS year,
                   MONTH(t.transaction_date) AS month,
                   COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0) AS totalIncome,
                   COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0) AS totalExpense
            FROM transactions t
            WHERE t.user_id = :userId
            GROUP BY YEAR(t.transaction_date), MONTH(t.transaction_date)
            ORDER BY YEAR(t.transaction_date) DESC, MONTH(t.transaction_date) DESC
            """, nativeQuery = true)
    List<MonthlyBalanceProjection> findMonthlyBalanceByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId")
    BigDecimal sumAmountByUserId(@Param("userId") Long userId);

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.user.id = :userId
              AND t.type = com.kriterion.entity.enums.TransactionType.EXPENSE
              AND (:categoryId IS NULL OR t.category.id = :categoryId)
              AND YEAR(t.transactionDate) = :year
              AND MONTH(t.transactionDate) = :month
            """)
    BigDecimal calculateSpentAmount(@Param("userId") Long userId,
                                    @Param("categoryId") Long categoryId,
                                    @Param("month") int month,
                                    @Param("year") int year);
}
