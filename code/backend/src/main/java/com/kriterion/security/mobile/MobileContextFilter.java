package com.kriterion.security.mobile;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class MobileContextFilter extends OncePerRequestFilter {

    private static final String HEADER_DEVICE_ID = "X-Device-Id";
    private static final String HEADER_PLATFORM = "X-Platform";
    private static final String HEADER_APP_VERSION = "X-App-Version";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String deviceId = request.getHeader(HEADER_DEVICE_ID);
        String platform = request.getHeader(HEADER_PLATFORM);
        String appVersion = request.getHeader(HEADER_APP_VERSION);

        if (deviceId != null || platform != null) {
            MobileContext context = MobileContext.builder()
                    .deviceId(deviceId)
                    .platform(platform)
                    .appVersion(appVersion)
                    .isMobileRequest(true)
                    .build();
            MobileContext.set(context);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            MobileContext.clear();
        }
    }
}
