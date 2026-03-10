package com.example.rces.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class DeviceDetector {

    public boolean isMobile(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent").toLowerCase();
        return userAgent.contains("mobi") || userAgent.contains("iphone") || userAgent.contains("android");
    }
}
