package com.kriterion.service.mobile;

import com.kriterion.entity.BaseEntity;
import com.kriterion.exception.ConflictException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SyncConflictService {

    /**
     * Checks for conflicts between client version and server version.
     * Throws ConflictException if a conflict is detected.
     */
    public void validateVersion(BaseEntity entity, Long clientVersion) {
        if (entity.getVersion() != null && clientVersion != null && !entity.getVersion().equals(clientVersion)) {
            log.warn("Sync conflict detected for entity {}. Server version: {}, Client version: {}", 
                    entity.getId(), entity.getVersion(), clientVersion);
            throw new ConflictException("Conflict detected. The record has been modified on another device.");
        }
    }

    /**
     * Practical resolution strategy: Last Write Wins (implicitly handled by JPA if we ignore versions),
     * or more complex merging logic.
     */
    public boolean resolveConflict(BaseEntity serverEntity, Object clientUpdate) {
        // Placeholder for more advanced resolution (e.g. field-level merging)
        return false;
    }
}
