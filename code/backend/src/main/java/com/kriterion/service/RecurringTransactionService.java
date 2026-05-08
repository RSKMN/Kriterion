package com.kriterion.service;

import com.kriterion.dto.recurring.RecurringTransactionRequest;
import com.kriterion.dto.recurring.RecurringTransactionResponse;
import com.kriterion.entity.Category;
import com.kriterion.entity.RecurringTransaction;
import com.kriterion.entity.Transaction;
import com.kriterion.entity.User;
import com.kriterion.exception.ApiException;
import com.kriterion.event.KriterionEventPublisher;
import com.kriterion.event.RecurringTransactionReminderEvent;
import com.kriterion.repository.CategoryRepository;
import com.kriterion.repository.RecurringTransactionRepository;
import com.kriterion.repository.TransactionRepository;
import com.kriterion.repository.UserRepository;
import com.kriterion.security.util.AuthenticationUtil;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecurringTransactionService {

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final KriterionEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public List<RecurringTransactionResponse> getAllRecurringTransactions() {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        return recurringTransactionRepository.findAllByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RecurringTransactionResponse createRecurringTransaction(RecurringTransactionRequest request) {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ApiException("Category not found", HttpStatus.NOT_FOUND));

        if (category.getUser() != null && !category.getUser().getId().equals(userId)) {
            throw new ApiException("Unauthorized category use", HttpStatus.FORBIDDEN);
        }

        RecurringTransaction rt = new RecurringTransaction();
        rt.setUser(user);
        rt.setCategory(category);
        rt.setTitle(request.getTitle());
        rt.setAmount(request.getAmount());
        rt.setType(request.getType());
        rt.setRecurrenceType(request.getRecurrenceType());
        rt.setPaymentMethod(request.getPaymentMethod());
        rt.setStartDate(request.getStartDate());
        rt.setEndDate(request.getEndDate());
        rt.setIsActive(request.getIsActive());
        
        // Initial next run date
        rt.setNextRunDate(request.getStartDate());

        RecurringTransaction saved = recurringTransactionRepository.save(rt);
        return mapToResponse(saved);
    }

    @Transactional
    public RecurringTransactionResponse updateRecurringTransaction(Long id, RecurringTransactionRequest request) {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        RecurringTransaction rt = recurringTransactionRepository.findById(id)
                .orElseThrow(() -> new ApiException("Recurring transaction not found", HttpStatus.NOT_FOUND));

        if (!rt.getUser().getId().equals(userId)) {
            throw new ApiException("Unauthorized access", HttpStatus.FORBIDDEN);
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ApiException("Category not found", HttpStatus.NOT_FOUND));

        rt.setCategory(category);
        rt.setTitle(request.getTitle());
        rt.setAmount(request.getAmount());
        rt.setType(request.getType());
        rt.setRecurrenceType(request.getRecurrenceType());
        rt.setPaymentMethod(request.getPaymentMethod());
        rt.setEndDate(request.getEndDate());
        rt.setIsActive(request.getIsActive());

        RecurringTransaction updated = recurringTransactionRepository.save(rt);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteRecurringTransaction(Long id) {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        RecurringTransaction rt = recurringTransactionRepository.findById(id)
                .orElseThrow(() -> new ApiException("Recurring transaction not found", HttpStatus.NOT_FOUND));

        if (!rt.getUser().getId().equals(userId)) {
            throw new ApiException("Unauthorized access", HttpStatus.FORBIDDEN);
        }

        recurringTransactionRepository.delete(rt);
    }

    @Transactional
    public void executeRecurringTransactions() {
        LocalDate today = LocalDate.now();
        List<RecurringTransaction> dueRules = recurringTransactionRepository.findAllByIsActiveTrueAndNextRunDateBefore(today.plusDays(1));

        log.info("Starting recurring transaction processing for {} rules", dueRules.size());

        for (RecurringTransaction rule : dueRules) {
            try {
                processRule(rule, today);
            } catch (Exception e) {
                log.error("Failed to process recurring rule {}: {}", rule.getId(), e.getMessage());
            }
        }
        
        log.info("Finished recurring transaction processing");
    }

    @Transactional
    public void sendReminders() {
        LocalDate horizon = LocalDate.now().plusDays(3);
        List<RecurringTransaction> upcoming = recurringTransactionRepository.findAllByIsActiveTrueAndNextRunDateBefore(horizon.plusDays(1));
        
        log.info("Checking for upcoming recurring transactions to notify users. Horizon: {}", horizon);
        
        for (RecurringTransaction rt : upcoming) {
            // We only send a reminder if it's within the next 3 days and hasn't been executed yet
            if (rt.getNextRunDate().isAfter(LocalDate.now().minusDays(1))) {
                eventPublisher.publishEvent(new RecurringTransactionReminderEvent(
                        this, rt.getUser().getId(), rt.getTitle(), rt.getAmount(), rt.getNextRunDate()));
            }
        }
    }

    private void processRule(RecurringTransaction rule, LocalDate today) {
        // Catch-up: generate transactions for all missed dates up to today
        while (rule.getNextRunDate().isBefore(today.plusDays(1)) && rule.getIsActive()) {
            LocalDate executionDate = rule.getNextRunDate();
            
            // Duplicate prevention
            boolean alreadyExists = transactionRepository.existsByRecurringTransactionIdAndTransactionDate(
                    rule.getId(), executionDate);
            
            if (!alreadyExists) {
                log.debug("Generating transaction for rule {} on date {}", rule.getId(), executionDate);
                createTransactionFromRule(rule, executionDate);
            } else {
                log.warn("Transaction already exists for rule {} on date {}. Skipping generation.", rule.getId(), executionDate);
            }

            // Advance next run date
            LocalDate nextRun = calculateNextRunDate(executionDate, rule.getRecurrenceType());
            rule.setNextRunDate(nextRun);

            // Termination check
            if (rule.getEndDate() != null && nextRun.isAfter(rule.getEndDate())) {
                rule.setIsActive(false);
                log.info("Rule {} has reached its end date ({}) and is now inactive.", rule.getId(), rule.getEndDate());
                break;
            }
        }
        recurringTransactionRepository.save(rule);
    }

    private void createTransactionFromRule(RecurringTransaction rule, LocalDate date) {
        Transaction transaction = new Transaction();
        transaction.setUser(rule.getUser());
        transaction.setCategory(rule.getCategory());
        transaction.setTitle(rule.getTitle());
        transaction.setAmount(rule.getAmount());
        transaction.setType(rule.getType());
        transaction.setPaymentMethod(rule.getPaymentMethod());
        transaction.setTransactionDate(date);
        transaction.setIsRecurring(true);
        transaction.setRecurringTransactionId(rule.getId());

        transactionRepository.save(transaction);
    }

    private LocalDate calculateNextRunDate(LocalDate currentRun, com.kriterion.entity.enums.RecurrenceType type) {
        return switch (type) {
            case DAILY -> currentRun.plusDays(1);
            case WEEKLY -> currentRun.plusWeeks(1);
            case MONTHLY -> currentRun.plusMonths(1);
            case YEARLY -> currentRun.plusYears(1);
        };
    }

    private RecurringTransactionResponse mapToResponse(RecurringTransaction rt) {
        return RecurringTransactionResponse.builder()
                .id(rt.getId())
                .title(rt.getTitle())
                .amount(rt.getAmount())
                .categoryId(rt.getCategory().getId())
                .categoryName(rt.getCategory().getName())
                .type(rt.getType())
                .recurrenceType(rt.getRecurrenceType())
                .paymentMethod(rt.getPaymentMethod())
                .startDate(rt.getStartDate())
                .endDate(rt.getEndDate())
                .nextRunDate(rt.getNextRunDate())
                .isActive(rt.getIsActive())
                .build();
    }
}
