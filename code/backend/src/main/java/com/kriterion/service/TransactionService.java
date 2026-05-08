package com.kriterion.service;

import com.kriterion.dto.transaction.CreateTransactionRequest;
import com.kriterion.dto.transaction.TransactionResponse;
import com.kriterion.dto.transaction.UpdateTransactionRequest;
import com.kriterion.entity.Category;
import com.kriterion.entity.Transaction;
import com.kriterion.entity.User;
import com.kriterion.exception.BadRequestException;
import com.kriterion.exception.NotFoundException;
import com.kriterion.exception.UnauthorizedException;
import com.kriterion.event.KriterionEventPublisher;
import com.kriterion.event.SuspiciousSpendingEvent;
import com.kriterion.repository.CategoryRepository;
import com.kriterion.repository.TransactionRepository;
import com.kriterion.repository.UserRepository;
import com.kriterion.security.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final KriterionEventPublisher eventPublisher;
    private final AnomalyDetectionService anomalyDetectionService;

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactions(Long categoryId, com.kriterion.entity.enums.TransactionType type, LocalDate startDate, LocalDate endDate, String search, Pageable pageable) {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        if (userId == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        org.springframework.data.jpa.domain.Specification<Transaction> spec = 
                com.kriterion.repository.specification.TransactionSpecification.withFilters(userId, categoryId, type, startDate, endDate, search);
                
        Page<Transaction> transactions = transactionRepository.findAll(spec, pageable);
        return transactions.map(this::mapToResponse);
    }

    @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        if (userId == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        if (category.getUser() != null && !category.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not have permission to use this category");
        }

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setTitle(request.getTitle());
        transaction.setDescription(request.getDescription());
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setMerchantName(request.getMerchantName());
        transaction.setLocation(request.getLocation());
        transaction.setIsRecurring(request.getIsRecurring());

        transaction = transactionRepository.save(transaction);

        // Perform anomaly detection
        try {
            anomalyDetectionService.detectAnomalies(transaction);
        } catch (Exception e) {
            log.error("Anomaly detection failed: {}", e.getMessage());
        }

        return mapToResponse(transaction);
    }

    @Transactional
    public TransactionResponse updateTransaction(Long id, UpdateTransactionRequest request) {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        if (userId == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction not found"));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You can only modify your own transactions");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        if (category.getUser() != null && !category.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not have permission to use this category");
        }

        transaction.setCategory(category);
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setTitle(request.getTitle());
        transaction.setDescription(request.getDescription());
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setMerchantName(request.getMerchantName());
        transaction.setLocation(request.getLocation());
        transaction.setIsRecurring(request.getIsRecurring());

        transaction = transactionRepository.save(transaction);
        return mapToResponse(transaction);
    }

    @Transactional
    public void deleteTransaction(Long id) {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        if (userId == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction not found"));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You can only delete your own transactions");
        }

        transactionRepository.delete(transaction);
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .userId(transaction.getUser().getId())
                .categoryId(transaction.getCategory().getId())
                .categoryName(transaction.getCategory().getName())
                .categoryColor(transaction.getCategory().getColor())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .title(transaction.getTitle())
                .description(transaction.getDescription())
                .paymentMethod(transaction.getPaymentMethod())
                .transactionDate(transaction.getTransactionDate())
                .isRecurring(transaction.getIsRecurring())
                .recurringTransactionId(transaction.getRecurringTransactionId())
                .receiptId(transaction.getReceiptId())
                .aiCategorized(transaction.getAiCategorized())
                .location(transaction.getLocation())
                .merchantName(transaction.getMerchantName())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}
