package com.kriterion.security.mobile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MobileContext {
    private String deviceId;
    private String platform;
    private String appVersion;
    private boolean isMobileRequest;

    private static final ThreadLocal<MobileContext> currentContext = new ThreadLocal<>();

    public static void set(MobileContext context) {
        currentContext.set(context);
    }

    public static MobileContext get() {
        return currentContext.get();
    }

    public static void clear() {
        currentContext.remove();
    }
}
