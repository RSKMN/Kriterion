package com.kriterion.demo;

import com.kriterion.entity.Category;
import com.kriterion.entity.Transaction;
import com.kriterion.entity.User;
import com.kriterion.entity.enums.PaymentMethod;
import com.kriterion.entity.enums.TransactionType;
import com.kriterion.repository.CategoryRepository;
import com.kriterion.repository.TransactionRepository;
import com.kriterion.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BehavioralDemoSeeder {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final Random random = new Random();

    @Transactional
    public void seedBehavioralData(Long userId) {
        log.info("Seeding behavioral demo data for user: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Ensure categories exist
        if (categoryRepository.count() == 0) {
            seedDefaultCategories();
        }

        // Clean existing transactions for demo
        List<Transaction> existing = transactionRepository.findByUserId(userId);
        transactionRepository.deleteAll(existing);

        List<Category> allCategories = categoryRepository.findAll();
        Category salaryCat = findCategory(allCategories, "Salary", "Income");
        Category foodCat = findCategory(allCategories, "Food", "Dining");
        Category shoppingCat = findCategory(allCategories, "Shopping", "Entertainment");
        Category billsCat = findCategory(allCategories, "Bills", "Utilities");
        Category transportCat = findCategory(allCategories, "Transport", "Travel");
        Category subCat = findCategory(allCategories, "Subscriptions", "Services");

        LocalDate today = LocalDate.now();
        List<Transaction> transactions = new ArrayList<>();

        // 1. Monthly Salaries (Temporal Consistency)
        for (int i = 0; i < 3; i++) {
            LocalDate date = today.minusMonths(i).withDayOfMonth(1);
            transactions.add(createTransaction(user, salaryCat, TransactionType.INCOME, 5000.0, "Monthly Salary", date, LocalTime.of(9, 0), false));
            
            // Post-Salary Spending Spike (First week)
            for (int d = 1; d <= 5; d++) {
                transactions.add(createTransaction(user, shoppingCat, TransactionType.EXPENSE, 100.0 + random.nextDouble() * 200, "Salary Treat", date.plusDays(d), LocalTime.of(18, 30), false));
            }
        }

        // 2. Weekly Groceries (Rhythm)
        for (int i = 0; i < 12; i++) {
            LocalDate date = today.minusWeeks(i).with(java.time.DayOfWeek.SATURDAY);
            transactions.add(createTransaction(user, foodCat, TransactionType.EXPENSE, 80.0 + random.nextDouble() * 40, "Weekly Groceries", date, LocalTime.of(11, 0), false));
        }

        // 3. Daily Coffee/Lunch (High Frequency, Fragmentation)
        for (int i = 0; i < 60; i++) {
            LocalDate date = today.minusDays(i);
            if (date.getDayOfWeek().getValue() <= 5) { // Work days
                transactions.add(createTransaction(user, foodCat, TransactionType.EXPENSE, 5.0 + random.nextDouble() * 10, "Office Coffee", date, LocalTime.of(8, 45), false));
                transactions.add(createTransaction(user, foodCat, TransactionType.EXPENSE, 12.0 + random.nextDouble() * 8, "Office Lunch", date, LocalTime.of(12, 30), false));
            }
        }

        // 4. Subscriptions (Recurring Obligation)
        transactions.add(createTransaction(user, subCat, TransactionType.EXPENSE, 15.99, "Netflix", today.withDayOfMonth(15), LocalTime.of(0, 5), true));
        transactions.add(createTransaction(user, subCat, TransactionType.EXPENSE, 9.99, "Spotify", today.withDayOfMonth(5), LocalTime.of(0, 5), true));
        transactions.add(createTransaction(user, subCat, TransactionType.EXPENSE, 119.0, "Amazon Prime", today.minusMonths(1).withDayOfMonth(20), LocalTime.of(0, 5), true));

        // 5. Late Night Spending Burst (High Volatility, Instability)
        for (int i = 0; i < 4; i++) {
            LocalDate date = today.minusWeeks(i).with(java.time.DayOfWeek.FRIDAY);
            transactions.add(createTransaction(user, foodCat, TransactionType.EXPENSE, 45.0, "Late Night Pizza", date, LocalTime.of(23, 45), false));
            transactions.add(createTransaction(user, transportCat, TransactionType.EXPENSE, 25.0, "Uber Home", date.plusDays(1), LocalTime.of(2, 15), false));
        }

        // 6. Large Irregular Purchase (Volatility Spike)
        transactions.add(createTransaction(user, shoppingCat, TransactionType.EXPENSE, 1200.0, "New Laptop", today.minusDays(45), LocalTime.of(14, 20), false));

        transactionRepository.saveAll(transactions);
        log.info("Seeded {} transactions for user: {}", transactions.size(), userId);
    }

    private void seedDefaultCategories() {
        String[] income = {"Salary", "Investment", "Freelance"};
        String[] expense = {"Food", "Shopping", "Bills", "Transport", "Subscriptions", "Entertainment", "Health"};
        
        for (String s : income) {
            Category c = new Category();
            c.setName(s);
            c.setType(com.kriterion.entity.enums.CategoryType.INCOME);
            categoryRepository.save(c);
        }
        for (String s : expense) {
            Category c = new Category();
            c.setName(s);
            c.setType(com.kriterion.entity.enums.CategoryType.EXPENSE);
            categoryRepository.save(c);
        }
    }

    private Category findCategory(List<Category> all, String... names) {
        for (String name : names) {
            for (Category c : all) {
                if (c.getName().equalsIgnoreCase(name)) return c;
            }
        }
        return all.get(0); // Fallback
    }

    private Transaction createTransaction(User user, Category category, TransactionType type, double amount, String title, LocalDate date, LocalTime time, boolean recurring) {
        Transaction t = new Transaction();
        t.setUser(user);
        t.setCategory(category);
        t.setType(type);
        t.setAmount(BigDecimal.valueOf(amount));
        t.setTitle(title);
        t.setTransactionDate(date);
        t.setTransactionTime(time);
        t.setIsRecurring(recurring);
        t.setPaymentMethod(PaymentMethod.CARD);
        return t;
    }
}
