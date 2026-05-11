package com.kriterion.service.mobile;

import com.kriterion.dto.mobile.*;
import com.kriterion.dto.transaction.CreateTransactionRequest;
import com.kriterion.dto.transaction.UpdateTransactionRequest;
import com.kriterion.dto.transaction.TransactionResponse;
import com.kriterion.dto.category.CategoryResponse;
import com.kriterion.dto.user.UserResponse;
import com.kriterion.entity.*;
import com.kriterion.repository.*;
import com.kriterion.analytics.AnalyticsService;
import com.kriterion.service.BudgetService;
import com.kriterion.service.CategoryService;
import com.kriterion.service.TransactionService;
import com.kriterion.security.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MobileSyncService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final SyncMetadataRepository syncMetadataRepository;
    
    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final BudgetService budgetService;
    private final AnalyticsService analyticsService;
    private final SyncConflictService syncConflictService;

    @Transactional(readOnly = true)
    public BootstrapResponse bootstrap() {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        User user = userRepository.findById(userId).orElseThrow();

        UserResponse profile = UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();

        List<CategoryResponse> categories = categoryService.getAllCategories();
        
        // budgets for current month
        LocalDateTime now = LocalDateTime.now();
        List<com.kriterion.dto.budget.BudgetResponse> budgets = budgetService.getBudgets(now.getMonthValue(), now.getYear());

        // analytics essentials
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("dashboardSummary", analyticsService.getDashboardSummary());

        return BootstrapResponse.builder()
                .profile(profile)
                .categories(categories)
                .budgets(budgets)
                .serverTime(now)
                .analyticsEssentials(analytics)
                .syncToken(UUID.randomUUID().toString())
                .build();
    }

    @Transactional
    public SyncResponse sync(SyncRequest request) {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        User user = userRepository.findById(userId).orElseThrow();
        LocalDateTime lastSyncTime = request.getLastSyncTime();
        
        List<SyncResponse.SyncAcknowledgment> acknowledgments = new ArrayList<>();

        // 1. Process Client Changes (Retry-safe / Idempotent)
        if (request.getChanges() != null) {
            processClientChanges(user, request.getChanges(), acknowledgments);
        }

        // 2. Fetch Server Changes
        SyncPayload serverChanges = getSyncPayload(userId, lastSyncTime);

        // 3. Update Sync Checkpoint
        String nextSyncToken = UUID.randomUUID().toString();
        SyncMetadata metadata = syncMetadataRepository.findByUserAndDeviceId(user, request.getDeviceId())
                .orElse(SyncMetadata.builder().user(user).deviceId(request.getDeviceId()).build());
        
        metadata.setLastSyncedAt(LocalDateTime.now());
        metadata.setSyncToken(nextSyncToken);
        syncMetadataRepository.save(metadata);

        return SyncResponse.builder()
                .syncTime(LocalDateTime.now())
                .serverChanges(serverChanges)
                .acknowledgments(acknowledgments)
                .build();
    }

    private void processClientChanges(User user, SyncChangeSet changes, List<SyncResponse.SyncAcknowledgment> acks) {
        // Handle new transactions (IDEMPOTENT via clientUuid)
        if (changes.getNewTransactions() != null) {
            for (CreateTransactionRequest req : changes.getNewTransactions()) {
                try {
                    // IDEMPOTENCY CHECK: deduplicate by clientUuid
                    Optional<Transaction> existing = transactionRepository.findByClientUuid(req.getClientUuid());
                    if (existing.isPresent()) {
                        acks.add(new SyncResponse.SyncAcknowledgment(req.getClientUuid(), existing.get().getId(), SyncResponse.SyncStatus.SUCCESS, null));
                        continue;
                    }
                    
                    TransactionResponse saved = transactionService.createTransaction(req);
                    // Manually update clientUuid
                    transactionRepository.findById(saved.getId()).ifPresent(t -> {
                        t.setClientUuid(req.getClientUuid());
                        transactionRepository.save(t);
                    });
                    
                    acks.add(new SyncResponse.SyncAcknowledgment(req.getClientUuid(), saved.getId(), SyncResponse.SyncStatus.SUCCESS, null));
                } catch (Exception e) {
                    log.error("Sync error (NEW): {}", e.getMessage());
                    acks.add(new SyncResponse.SyncAcknowledgment(req.getClientUuid(), null, SyncResponse.SyncStatus.ERROR, e.getMessage()));
                }
            }
        }

        // Handle updates (CONFLICT-SAFE via version)
        if (changes.getUpdatedTransactions() != null) {
            for (UpdateTransactionRequest req : changes.getUpdatedTransactions()) {
                try {
                    Transaction transaction = transactionRepository.findById(req.getId())
                            .orElseThrow(() -> new com.kriterion.exception.NotFoundException("Transaction not found: " + req.getId()));

                    // CONFLICT CHECK
                    syncConflictService.validateVersion(transaction, req.getVersion());

                    TransactionResponse updated = transactionService.updateTransaction(req.getId(), req);
                    acks.add(new SyncResponse.SyncAcknowledgment(req.getClientUuid(), updated.getId(), SyncResponse.SyncStatus.SUCCESS, null));
                } catch (com.kriterion.exception.ConflictException e) {
                    acks.add(new SyncResponse.SyncAcknowledgment(req.getClientUuid(), req.getId(), SyncResponse.SyncStatus.CONFLICT, e.getMessage()));
                } catch (Exception e) {
                    acks.add(new SyncResponse.SyncAcknowledgment(req.getClientUuid(), req.getId(), SyncResponse.SyncStatus.ERROR, e.getMessage()));
                }
            }
        }

        // Handle deletes
        if (changes.getDeletedTransactionIds() != null) {
            for (Long id : changes.getDeletedTransactionIds()) {
                try {
                    transactionService.deleteTransaction(id);
                } catch (Exception e) {
                    log.warn("Failed to delete transaction {} during sync: {}", id, e.getMessage());
                }
            }
        }
    }

    private SyncPayload getSyncPayload(Long userId, LocalDateTime lastSync) {
        if (lastSync == null) lastSync = LocalDateTime.now().minusYears(1);

        List<TransactionResponse> transactions = transactionRepository
                .findByUserIdAndUpdatedAtAfter(userId, lastSync)
                .stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());

        List<CategoryResponse> categories = categoryRepository
                .findByUserIdAndUpdatedAtAfter(userId, lastSync)
                .stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());

        List<Long> deletedTransactionIds = transactionRepository
                .findByUserIdAndDeletedAtAfter(userId, lastSync)
                .stream()
                .map(Transaction::getId)
                .collect(Collectors.toList());

        return SyncPayload.builder()
                .syncTime(LocalDateTime.now())
                .transactions(transactions)
                .categories(categories)
                .deletedTransactionIds(deletedTransactionIds)
                .build();
    }

    private TransactionResponse mapToTransactionResponse(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .clientUuid(t.getClientUuid())
                .userId(t.getUser().getId())
                .categoryId(t.getCategory().getId())
                .categoryName(t.getCategory().getName())
                .type(t.getType())
                .amount(t.getAmount())
                .title(t.getTitle())
                .description(t.getDescription())
                .paymentMethod(t.getPaymentMethod())
                .transactionDate(t.getTransactionDate())
                .isRecurring(t.getIsRecurring())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .version(t.getVersion())
                .build();
    }

    private CategoryResponse mapToCategoryResponse(Category c) {
        return CategoryResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .type(c.getType())
                .icon(c.getIcon())
                .color(c.getColor())
                .isDefault(c.getIsDefault())
                .createdAt(c.getCreatedAt())
                .version(c.getVersion())
                .build();
    }
}
