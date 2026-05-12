package com.kriterion.predictivelite.service;

import com.kriterion.entity.Category;
import com.kriterion.entity.Transaction;
import com.kriterion.entity.User;
import com.kriterion.entity.enums.CategoryType;
import com.kriterion.entity.enums.PaymentMethod;
import com.kriterion.entity.enums.TransactionType;
import com.kriterion.repository.CategoryRepository;
import com.kriterion.repository.TransactionRepository;
import com.kriterion.repository.UserRepository;
import com.kriterion.security.util.AuthenticationUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PredictiveAnalyticsLiteSeeder {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private static final Random RANDOM = new Random();

    public boolean seedIfNeeded() {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        if (userId == null) {
            return false;
        }

        long count = transactionRepository.countByUserId(userId);
        if (count >= 30) {
            return false;
        }

        seedDemoData(userId);
        return true;
    }

    @Transactional
    private void seedDemoData(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        LocalDate today = LocalDate.now();

        // Get or create categories
        Category salary = findOrCreateCategory("Salary", CategoryType.INCOME, "#4f46e5");
        Category groceries = findOrCreateCategory("Groceries", CategoryType.EXPENSE, "#16a34a");
        Category transport = findOrCreateCategory("Transport", CategoryType.EXPENSE, "#0ea5e9");
        Category dining = findOrCreateCategory("Dining", CategoryType.EXPENSE, "#f97316");
        Category subscriptions = findOrCreateCategory("Subscriptions", CategoryType.EXPENSE, "#8b5cf6");
        Category entertainment = findOrCreateCategory("Entertainment", CategoryType.EXPENSE, "#d946ef");
        Category shopping = findOrCreateCategory("Shopping", CategoryType.EXPENSE, "#ef4444");

        // Salary transactions (monthly pattern)
        for (int i = 0; i < 3; i++) {
            LocalDate salaryDate = today.minusDays(30 * i);
            createTransaction(user, salary, salaryDate, LocalTime.of(9, 0), "Monthly Salary", new BigDecimal("3500.00"), TransactionType.INCOME, false);
        }

        // Groceries (2-3 times per week, variable amounts)
        for (int i = 0; i < 12; i++) {
            LocalDate groceryDate = today.minusDays(RANDOM.nextInt(60) + 1);
            BigDecimal amount = new BigDecimal(String.format("%.2f", RANDOM.nextDouble() * 60 + 40));
            createTransaction(user, groceries, groceryDate, LocalTime.of(18, RANDOM.nextInt(60)), "Groceries", amount, TransactionType.EXPENSE, false);
        }

        // Transport/Fuel (weekly pattern)
        for (int i = 0; i < 8; i++) {
            LocalDate transportDate = today.minusDays(i * 7 + RANDOM.nextInt(2));
            BigDecimal amount = new BigDecimal(String.format("%.2f", RANDOM.nextDouble() * 30 + 35));
            createTransaction(user, transport, transportDate, LocalTime.of(17, RANDOM.nextInt(60)), "Fuel", amount, TransactionType.EXPENSE, false);
        }

        // Dining out (1-2 times per week, variable)
        for (int i = 0; i < 10; i++) {
            LocalDate diningDate = today.minusDays(RANDOM.nextInt(60) + 1);
            LocalTime diningTime = LocalTime.of(12 + RANDOM.nextInt(2), RANDOM.nextInt(60));
            BigDecimal amount = new BigDecimal(String.format("%.2f", RANDOM.nextDouble() * 40 + 15));
            createTransaction(user, dining, diningDate, diningTime, "Restaurant", amount, TransactionType.EXPENSE, false);
        }

        // Subscriptions (recurring, consistent)
        createTransaction(user, subscriptions, today.minusDays(5), LocalTime.of(2, 0), "Netflix", new BigDecimal("15.99"), TransactionType.EXPENSE, true);
        createTransaction(user, subscriptions, today.minusDays(10), LocalTime.of(3, 0), "Spotify", new BigDecimal("9.99"), TransactionType.EXPENSE, true);
        createTransaction(user, subscriptions, today.minusDays(8), LocalTime.of(2, 30), "Cloud Storage", new BigDecimal("2.99"), TransactionType.EXPENSE, true);
        createTransaction(user, subscriptions, today.minusDays(15), LocalTime.of(1, 0), "Gym Membership", new BigDecimal("49.99"), TransactionType.EXPENSE, true);

        // Late-night transactions (11 PM - 5 AM)
        for (int i = 0; i < 8; i++) {
            LocalDate lateDate = today.minusDays(RANDOM.nextInt(45) + 1);
            LocalTime lateTime = RANDOM.nextBoolean() 
                ? LocalTime.of(23 + RANDOM.nextInt(1), RANDOM.nextInt(60))
                : LocalTime.of(RANDOM.nextInt(5), RANDOM.nextInt(60));
            BigDecimal amount = new BigDecimal(String.format("%.2f", RANDOM.nextDouble() * 50 + 10));
            createTransaction(user, entertainment, lateDate, lateTime, "Late Night", amount, TransactionType.EXPENSE, false);
        }

        // Shopping (sporadic, higher amounts)
        for (int i = 0; i < 6; i++) {
            LocalDate shoppingDate = today.minusDays(RANDOM.nextInt(40) + 1);
            BigDecimal amount = new BigDecimal(String.format("%.2f", RANDOM.nextDouble() * 120 + 50));
            createTransaction(user, shopping, shoppingDate, LocalTime.of(15, RANDOM.nextInt(60)), "Shopping", amount, TransactionType.EXPENSE, false);
        }

        // End-of-month spike (last 7 days of previous months)
        for (int month = 0; month < 2; month++) {
            LocalDate monthEnd = today.minusMonths(month).withDayOfMonth(today.minusMonths(month).lengthOfMonth());
            for (int day = 0; day < 3; day++) {
                LocalDate spikeDate = monthEnd.minusDays(day);
                BigDecimal amount = new BigDecimal(String.format("%.2f", RANDOM.nextDouble() * 100 + 30));
                createTransaction(user, shopping, spikeDate, LocalTime.of(14, RANDOM.nextInt(60)), "End-of-Month Shopping", amount, TransactionType.EXPENSE, false);
            }
        }

        // Spending bursts (rapid multiple transactions)
        LocalDate burstDate = today.minusDays(RANDOM.nextInt(30) + 1);
        for (int j = 0; j < 5; j++) {
            LocalTime burstTime = LocalTime.of(19 + RANDOM.nextInt(5), RANDOM.nextInt(60));
            BigDecimal amount = new BigDecimal(String.format("%.2f", RANDOM.nextDouble() * 40 + 10));
            createTransaction(user, entertainment, burstDate, burstTime, "Burst", amount, TransactionType.EXPENSE, false);
        }

        log.info("Seeded demo transactions for user {}", userId);
    }

    private Category findOrCreateCategory(String name, CategoryType type, String color) {
        return categoryRepository.findByType(type).stream()
                .filter(category -> category.getUser() == null && category.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> {
                    Category category = new Category();
                    category.setName(name);
                    category.setType(type);
                    category.setColor(color);
                    category.setIsDefault(true);
                    return categoryRepository.save(category);
                });
    }

    private void createTransaction(User user, Category category, LocalDate date, LocalTime time, String title, BigDecimal amount, TransactionType type, Boolean isRecurring) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setTransactionDate(date);
        transaction.setTransactionTime(time);
        transaction.setTitle(title);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setIsRecurring(isRecurring);
        transaction.setPaymentMethod(PaymentMethod.CARD);
        transaction.setAiCategorized(false);
        transactionRepository.save(transaction);
    }
}
