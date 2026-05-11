package com.kriterion.dto.mobile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FcmTokenRegistrationRequest {
    @NotBlank(message = "FCM token is required")
    private String token;
    private String deviceId;
    private String platform;
}
