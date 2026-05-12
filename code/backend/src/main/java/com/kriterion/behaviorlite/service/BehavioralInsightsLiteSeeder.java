package com.kriterion.behaviorlite.service;

import com.kriterion.entity.Category;
import com.kriterion.entity.Transaction;
import com.kriterion.entity.User;
import com.kriterion.entity.enums.CategoryType;
import com.kriterion.entity.enums.PaymentMethod;
import com.kriterion.entity.enums.TransactionType;
import com.kriterion.repository.CategoryRepository;
import com.kriterion.repository.TransactionRepository;
import com.kriterion.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BehavioralInsightsLiteSeeder {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final Random random = new Random(41);

    @Transactional
    public boolean seedIfNeeded(Long userId) {
        if (userId == null) {
            return false;
        }

        long existingCount = transactionRepository.countByUserId(userId);
        if (existingCount >= 24) {
            return false;
        }

        seedDemoTransactions(userId);
        return true;
    }

    @Transactional
    public void seedDemoTransactions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        Category salary = findOrCreateCategory("Salary", CategoryType.INCOME, userId, "#4f46e5");
        Category groceries = findOrCreateCategory("Groceries", CategoryType.EXPENSE, userId, "#16a34a");
        Category transport = findOrCreateCategory("Transport", CategoryType.EXPENSE, userId, "#0ea5e9");
        Category dining = findOrCreateCategory("Dining", CategoryType.EXPENSE, userId, "#f97316");
        Category subscriptions = findOrCreateCategory("Subscriptions", CategoryType.EXPENSE, userId, "#8b5cf6");
        Category shopping = findOrCreateCategory("Shopping", CategoryType.EXPENSE, userId, "#ef4444");

        List<Transaction> existing = transactionRepository.findByUserId(userId);
        if (!existing.isEmpty()) {
            existing.sort(Comparator.comparing(Transaction::getTransactionDate).reversed());
        }

        List<Transaction> demoTransactions = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int monthOffset = 0; monthOffset < 3; monthOffset++) {
            LocalDate salaryDate = today.minusMonths(monthOffset).withDayOfMonth(1);
            demoTransactions.add(buildTransaction(user, salary, TransactionType.INCOME, 5200.0, "Monthly Salary", salaryDate, LocalTime.of(9, 0), false));
            demoTransactions.add(buildTransaction(user, salary, TransactionType.INCOME, 1200.0, "Side Income", salaryDate.plusDays(12), LocalTime.of(10, 15), false));

            for (int day = 1; day <= 5; day++) {
                demoTransactions.add(buildTransaction(user, shopping, TransactionType.EXPENSE, 95.0 + (day * 13) + random.nextDouble() * 40, "Salary Treat", salaryDate.plusDays(day), LocalTime.of(19, 10), false));
            }
        }

        for (int week = 0; week < 8; week++) {
            LocalDate weekDate = today.minusWeeks(week).with(java.time.DayOfWeek.SATURDAY);
            demoTransactions.add(buildTransaction(user, groceries, TransactionType.EXPENSE, 88.0 + random.nextDouble() * 35, "Weekly Groceries", weekDate, LocalTime.of(11, 30), false));
            demoTransactions.add(buildTransaction(user, transport, TransactionType.EXPENSE, 16.0 + random.nextDouble() * 12, "Commuting", weekDate.plusDays(1), LocalTime.of(8, 10), false));
        }

        for (int day = 0; day < 30; day += 2) {
            LocalDate date = today.minusDays(day);
            demoTransactions.add(buildTransaction(user, dining, TransactionType.EXPENSE, 12.0 + random.nextDouble() * 14, "Lunch", date, LocalTime.of(12, 45), false));
        }

        demoTransactions.add(buildTransaction(user, subscriptions, TransactionType.EXPENSE, 19.99, "Streaming", today.withDayOfMonth(5), LocalTime.of(0, 5), true));
        demoTransactions.add(buildTransaction(user, subscriptions, TransactionType.EXPENSE, 11.99, "Music", today.withDayOfMonth(12), LocalTime.of(0, 10), true));
        demoTransactions.add(buildTransaction(user, subscriptions, TransactionType.EXPENSE, 7.99, "Storage", today.withDayOfMonth(21), LocalTime.of(0, 15), true));

        for (int burst = 0; burst < 4; burst++) {
            LocalDate date = today.minusWeeks(burst).with(java.time.DayOfWeek.FRIDAY);
            demoTransactions.add(buildTransaction(user, dining, TransactionType.EXPENSE, 22.0, "Late Dinner", date, LocalTime.of(23, 30), false));
            demoTransactions.add(buildTransaction(user, dining, TransactionType.EXPENSE, 18.0, "Snack", date, LocalTime.of(23, 55), false));
            demoTransactions.add(buildTransaction(user, transport, TransactionType.EXPENSE, 27.0, "Ride Home", date.plusDays(1), LocalTime.of(1, 20), false));
            demoTransactions.add(buildTransaction(user, shopping, TransactionType.EXPENSE, 64.0 + random.nextDouble() * 120, "Weekend Burst", date.plusDays(1), LocalTime.of(2, 5), false));
        }

        transactionRepository.saveAll(demoTransactions);
        log.info("Seeded {} lite demo transactions for user {}", demoTransactions.size(), userId);
    }

    private Category findOrCreateCategory(String name, CategoryType type, Long userId, String color) {
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

    private Transaction buildTransaction(User user, Category category, TransactionType type, double amount, String title, LocalDate date, LocalTime time, boolean recurring) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setType(type);
        transaction.setAmount(BigDecimal.valueOf(amount));
        transaction.setTitle(title);
        transaction.setTransactionDate(date);
        transaction.setTransactionTime(time);
        transaction.setIsRecurring(recurring);
        transaction.setPaymentMethod(PaymentMethod.CARD);
        transaction.setAiCategorized(Boolean.TRUE);
        return transaction;
    }
}