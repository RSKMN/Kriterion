package com.kriterion.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kriterion.dto.backup.BackupData;
import com.kriterion.entity.BackupMetadata;
import com.kriterion.repository.BackupMetadataRepository;
import com.kriterion.security.util.AuthenticationUtil;
import com.kriterion.service.RecurringTransactionService;
import com.kriterion.service.TransactionService;
import com.kriterion.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BackupService {

    private final CategoryService categoryService;
    private final TransactionService transactionService;
    private final BudgetService budgetService;
    private final RecurringTransactionService recurringTransactionService;
    private final BackupMetadataRepository backupMetadataRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public byte[] generateBackup() {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        
        LocalDateTime now = LocalDateTime.now();
        
        BackupData data = BackupData.builder()
                .categories(categoryService.getAllCategories())
                .transactions(transactionService.getTransactions(null, null, null, null, null, Pageable.unpaged()).getContent())
                .budgets(budgetService.getBudgets(now.getMonthValue(), now.getYear()))
                .recurringTransactions(recurringTransactionService.getAllRecurringTransactions())
                .version("1.0")
                .exportDate(now.toString())
                .build();

        try {
            byte[] jsonBytes = objectMapper.writeValueAsBytes(data);
            
            // Track metadata
            BackupMetadata metadata = new BackupMetadata();
            metadata.setUserId(userId);
            metadata.setFileName("kriterion_backup_" + now.toLocalDate() + ".json");
            metadata.setFileSize((long) jsonBytes.length);
            metadata.setStatus("COMPLETED");
            backupMetadataRepository.save(metadata);
            
            return jsonBytes;
        } catch (Exception e) {
            throw new RuntimeException("Backup generation failed", e);
        }
    }

    @Transactional(readOnly = true)
    public List<BackupMetadata> getBackupHistory() {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        return backupMetadataRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }
}
