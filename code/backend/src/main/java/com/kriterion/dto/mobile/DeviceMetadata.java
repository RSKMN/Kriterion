package com.kriterion.dto.mobile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceMetadata {
    private String deviceId;
    private String deviceName;
    private String platform;   // e.g. android, ios, web
    private String osVersion;
    private String osType;     // keeping for compatibility
    private String appVersion;
    private String ipAddress;
}
