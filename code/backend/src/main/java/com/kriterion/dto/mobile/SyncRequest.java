package com.kriterion.dto.mobile;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SyncRequest {
    private LocalDateTime lastSyncTime;
    private String deviceId;
    private SyncChangeSet changes;
}
