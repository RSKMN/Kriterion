package com.kriterion.dto.mobile;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncResponse {
    private LocalDateTime syncTime;
    private SyncPayload serverChanges;
    private List<SyncAcknowledgment> acknowledgments;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SyncAcknowledgment {
        private String clientUuid;
        private Long serverId;
        private SyncStatus status;
        private String errorMessage;
    }

    public enum SyncStatus {
        SUCCESS, CONFLICT, ERROR
    }
}
