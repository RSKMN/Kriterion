package com.kriterion.service.backup;

import com.kriterion.dto.backup.BackupData;
import org.springframework.stereotype.Component;

@Component
public class RestoreValidationHelper {

    /**
     * Validates if the backup data is compatible and contains required fields.
     */
    public boolean isValid(BackupData data) {
        if (data == null) return false;
        
        // Basic version check
        if (data.getVersion() == null || !data.getVersion().startsWith("1.")) {
            return false;
        }

        // Must have at least categories or transactions structure (even if empty)
        return data.getCategories() != null && data.getTransactions() != null;
    }
    
    /**
     * Check for potential data corruption or size issues.
     */
    public void validateIntegrity(BackupData data) {
        if (data.getTransactions().size() > 100000) {
            throw new IllegalArgumentException("Backup file exceeds maximum transaction limit for restoration");
        }
    }
}
