package com.kriterion.financialreflectionlite.service;

import com.kriterion.entity.Category;
import com.kriterion.repository.CategoryRepository;
import com.kriterion.entity.enums.CategoryType;
import com.kriterion.entity.enums.PaymentMethod;
import com.kriterion.entity.enums.TransactionType;
import com.kriterion.entity.Transaction;
import com.kriterion.repository.TransactionRepository;
import com.kriterion.entity.User;
import com.kriterion.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class FinancialReflectionLiteSeeder {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final Random random = new Random();

    @Transactional
    public void seedDemoRhythms(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // Check if user already has enough transactions
        long count = transactionRepository.countByUserId(userId);
        if (count > 10) {
            log.info("User {} already has {} transactions, skipping rhythm seeding", userId, count);
            return;
        }

        log.info("Seeding demo financial rhythms for user {}", userId);

        Category incomeCat = findOrCreateCategory("Salary", CategoryType.INCOME, userId);
        Category groceriesCat = findOrCreateCategory("Groceries", CategoryType.EXPENSE, userId);
        Category subscriptionsCat = findOrCreateCategory("Subscriptions", CategoryType.EXPENSE, userId);
        Category diningCat = findOrCreateCategory("Dining", CategoryType.EXPENSE, userId);
        Category shoppingCat = findOrCreateCategory("Shopping", CategoryType.EXPENSE, userId);

        List<Transaction> demoTransactions = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // 1. Recurring Salary (Monthly)
        for (int i = 0; i < 3; i++) {
            demoTransactions.add(createTransaction(user, incomeCat, "Monthly Salary", 
                    new BigDecimal("4500.00"), today.minusMonths(i).withDayOfMonth(1), LocalTime.of(9, 0)));
        }

        // 2. Weekly Groceries (Regular rhythm)
        for (int i = 0; i < 12; i++) {
            demoTransactions.add(createTransaction(user, groceriesCat, "Weekly Groceries", 
                    new BigDecimal(80 + random.nextInt(40)), today.minusWeeks(i).with(java.time.DayOfWeek.SATURDAY), LocalTime.of(11, 0)));
        }

        // 3. Monthly Subscriptions (Fixed structure)
        demoTransactions.add(createTransaction(user, subscriptionsCat, "Netflix Subscription", 
                new BigDecimal("15.99"), today.withDayOfMonth(5), LocalTime.of(0, 5)));
        demoTransactions.add(createTransaction(user, subscriptionsCat, "Spotify Family", 
                new BigDecimal("16.99"), today.withDayOfMonth(12), LocalTime.of(0, 10)));
        demoTransactions.add(createTransaction(user, subscriptionsCat, "Cloud Storage", 
                new BigDecimal("9.99"), today.withDayOfMonth(20), LocalTime.of(0, 15)));

        // 4. Shopping Bursts (Density & Volatility)
        for (int i = 0; i < 3; i++) {
            LocalDate burstDate = today.minusWeeks(i * 2 + 1).with(java.time.DayOfWeek.FRIDAY);
            for (int j = 0; j < 4; j++) {
                demoTransactions.add(createTransaction(user, shoppingCat, "Online Purchase", 
                        new BigDecimal(20 + random.nextInt(100)), burstDate, LocalTime.of(22, 0 + (j * 15))));
            }
        }

        // 5. Late-Night Dining (Temporal pattern)
        for (int i = 0; i < 5; i++) {
            demoTransactions.add(createTransaction(user, diningCat, "Late Night Snack", 
                    new BigDecimal(15 + random.nextInt(15)), today.minusDays(i * 4 + 2), LocalTime.of(23, 45)));
        }

        transactionRepository.saveAll(demoTransactions);
        log.info("Successfully seeded {} demo transactions for user {}", demoTransactions.size(), userId);
    }

    private Transaction createTransaction(User user, Category category, String description, BigDecimal amount, LocalDate date, LocalTime time) {
        Transaction t = new Transaction();
        t.setUser(user);
        t.setCategory(category);
        t.setTitle(description);
        t.setDescription(description);
        t.setAmount(amount);
        t.setTransactionDate(date);
        t.setTransactionTime(time);
        t.setType(category.getType() == CategoryType.INCOME ? TransactionType.INCOME : TransactionType.EXPENSE);
        t.setPaymentMethod(PaymentMethod.CARD);
        return t;
    }

    private Category findOrCreateCategory(String name, CategoryType type, Long userId) {
        return categoryRepository.findByNameAndUserId(name, userId)
                .orElseGet(() -> {
                    Category c = new Category();
                    c.setName(name);
                    c.setType(type);
                    c.setUser(userRepository.findById(userId).orElse(null));
                    c.setColor("#cccccc");
                    return categoryRepository.save(c);
                });
    }
}
