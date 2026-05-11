package com.kriterion.controller.mobile;

import com.kriterion.analytics.AnalyticsService;
import com.kriterion.dto.analytics.DashboardSummaryResponse;
import com.kriterion.dto.mobile.DeviceMetadata;
import com.kriterion.dto.mobile.FcmTokenRegistrationRequest;
import com.kriterion.dto.mobile.SyncPayload;
import com.kriterion.dto.mobile.SyncRequest;
import com.kriterion.dto.shared.ApiResponse;
import com.kriterion.service.mobile.MobileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/v1/mobile")
@Tag(name = "Mobile APIs", description = "Endpoints specifically optimized for the Flutter mobile application.")
public class MobileController {

    private final AnalyticsService analyticsService;
    private final MobileService mobileService;
    private final com.kriterion.service.mobile.MobileSyncService syncService;
    private final com.kriterion.service.SessionService sessionService;

    public MobileController(AnalyticsService analyticsService, 
                            MobileService mobileService, 
                            com.kriterion.service.mobile.MobileSyncService syncService, 
                            com.kriterion.service.SessionService sessionService) {
        this.analyticsService = analyticsService;
        this.mobileService = mobileService;
        this.syncService = syncService;
        this.sessionService = sessionService;
    }

    @Operation(summary = "Get mobile config", description = "Returns mobile-specific configuration, feature flags, and API versioning.")
    @GetMapping("/config")
    public ResponseEntity<ApiResponse<Object>> getMobileConfig() {
        return ResponseEntity.ok(ApiResponse.success("Mobile configuration retrieved", 
            Collections.singletonMap("apiVersion", "v1")));
    }

    @Operation(summary = "Bootstrap mobile app", description = "Initial data dump for a new mobile device or after cache clear.")
    @GetMapping("/bootstrap")
    public ResponseEntity<ApiResponse<com.kriterion.dto.mobile.BootstrapResponse>> bootstrap() {
        return ResponseEntity.ok(ApiResponse.success("Bootstrap data retrieved", syncService.bootstrap()));
    }

    @Operation(summary = "Get home summary", description = "A mobile-optimized version of the dashboard summary for fast loading.")
    @GetMapping("/home-summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getHomeSummary() {
        DashboardSummaryResponse summary = analyticsService.getDashboardSummary();
        return ResponseEntity.ok(ApiResponse.success("Home summary retrieved", summary));
    }

    @Operation(summary = "Register device", description = "Registers or updates mobile device metadata for session tracking and security.")
    @PostMapping("/register-device")
    public ResponseEntity<ApiResponse<Void>> registerDevice(@RequestBody DeviceMetadata metadata) {
        mobileService.registerDevice(metadata);
        return ResponseEntity.ok(ApiResponse.success("Device registered successfully"));
    }

    @Operation(summary = "Refresh token", description = "Uses a refresh token to obtain a new access token and rotate the refresh token.")
    @PostMapping("/auth/refresh")
    public ResponseEntity<ApiResponse<java.util.Map<String, String>>> refreshToken(@RequestBody com.kriterion.dto.mobile.TokenRefreshRequest request) {
        String newRefreshToken = sessionService.refreshSession(request.getRefreshToken());
        java.util.Map<String, String> tokens = new java.util.HashMap<>();
        tokens.put("refreshToken", newRefreshToken);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", tokens));
    }

    @Operation(summary = "Get active sessions", description = "Retrieves a list of all active mobile sessions/devices for the authenticated user.")
    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse<java.util.List<com.kriterion.entity.MobileSession>>> getSessions() {
        Long userId = com.kriterion.security.util.AuthenticationUtil.getAuthenticatedUserIdAsLong();
        return ResponseEntity.ok(ApiResponse.success("Active sessions retrieved", sessionService.getActiveSessions(userId)));
    }

    @Operation(summary = "Revoke session", description = "Immediately invalidates a specific mobile session and its associated refresh tokens.")
    @DeleteMapping("/sessions/{id}")
    public ResponseEntity<ApiResponse<Void>> revokeSession(@PathVariable Long id) {
        sessionService.revokeSession(id);
        return ResponseEntity.ok(ApiResponse.success("Session revoked successfully"));
    }

    @Operation(summary = "Revoke all sessions", description = "Invalidates ALL active mobile sessions and refresh tokens for the current user (Force logout everywhere).")
    @DeleteMapping("/sessions/revoke-all")
    public ResponseEntity<ApiResponse<Void>> revokeAllSessions() {
        Long userId = com.kriterion.security.util.AuthenticationUtil.getAuthenticatedUserIdAsLong();
        sessionService.revokeAllSessions(userId);
        return ResponseEntity.ok(ApiResponse.success("All sessions revoked successfully"));
    }

    @Operation(summary = "Register FCM token", description = "Registers a new FCM token for push notifications.")
    @PostMapping("/fcm/register")
    public ResponseEntity<ApiResponse<Void>> registerFcmToken(@RequestBody FcmTokenRegistrationRequest request) {
        mobileService.registerFcmToken(request);
        return ResponseEntity.ok(ApiResponse.success("FCM token registered successfully"));
    }

    @Operation(summary = "Unregister FCM token", description = "Invalidates an FCM token.")
    @DeleteMapping("/fcm/unregister")
    public ResponseEntity<ApiResponse<Void>> unregisterFcmToken(@RequestParam String token) {
        mobileService.unregisterFcmToken(token);
        return ResponseEntity.ok(ApiResponse.success("FCM token unregistered successfully"));
    }

    @Operation(summary = "Update FCM token", description = "Updates an existing FCM token registration.")
    @PutMapping("/fcm/update")
    public ResponseEntity<ApiResponse<Void>> updateFcmToken(@RequestBody FcmTokenRegistrationRequest request) {
        mobileService.registerFcmToken(request);
        return ResponseEntity.ok(ApiResponse.success("FCM token updated successfully"));
    }

    @Operation(summary = "Sync data", description = "Core bidirectional synchronization endpoint for offline-first mobile workflows. Uploads changes and pulls server updates.")
    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<com.kriterion.dto.mobile.SyncResponse>> syncData(@RequestBody SyncRequest request) {
        com.kriterion.dto.mobile.SyncResponse response = syncService.sync(request);
        return ResponseEntity.ok(ApiResponse.success("Synchronization successful", response));
    }

    @Operation(summary = "Get pending transactions", description = "Returns transactions that are currently being processed or pending approval on the server.")
    @GetMapping("/pending-transactions")
    public ResponseEntity<ApiResponse<java.util.List<com.kriterion.dto.transaction.TransactionResponse>>> getPendingTransactions() {
        // Placeholder for real logic
        return ResponseEntity.ok(ApiResponse.success("Pending transactions retrieved", Collections.emptyList()));
    }
}
