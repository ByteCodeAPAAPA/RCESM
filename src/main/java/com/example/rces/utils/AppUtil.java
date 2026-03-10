package com.example.rces.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AppUtil implements LogoutHandler {
    private static final Map<String, UserProperties> userPropertiesMap = new ConcurrentHashMap<>();
    private static final long CLEANUP_DELAY_MS = 10_000; // 10 секунд

    private static class UserProperties {
        private String string;
        private Boolean bool;
        private Timer stringCleanupTimer;
        private Timer boolCleanupTimer;

        private void cancelTimers() {
            if (stringCleanupTimer != null) stringCleanupTimer.cancel();
            if (boolCleanupTimer != null) boolCleanupTimer.cancel();
        }
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
                       Authentication authentication) {
        if (authentication != null) {
            cleanupForUser(authentication.getName());
        }
    }

    public static void cleanupForUser(String userId) {
        UserProperties props = userPropertiesMap.remove(userId);
        if (props != null) {
            props.cancelTimers();
        }
    }

    private static UserProperties getCurrentUserProperties() {
        String userId = getCurrentUserId();
        return userPropertiesMap.computeIfAbsent(userId, k -> new UserProperties());
    }

    private static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "anonymous";
    }

    public static String getString() {
        UserProperties props = getCurrentUserProperties();
        cancelTimer(props.stringCleanupTimer);
        String value = props.string;
        props.string = null;
        return value;
    }

    public static void setString(String string) {
        UserProperties props = getCurrentUserProperties();
        props.string = string;
        scheduleCleanup(() -> props.string = null, props, true);
    }

    public static Boolean getBool() {
        UserProperties props = getCurrentUserProperties();
        cancelTimer(props.boolCleanupTimer);
        Boolean value = props.bool;
        props.bool = null;
        return value;
    }

    public static void setBool(Boolean bool) {
        UserProperties props = getCurrentUserProperties();
        props.bool = bool;
        scheduleCleanup(() -> props.bool = null, props, false);
    }

    private static void scheduleCleanup(Runnable cleanupAction, UserProperties props, boolean isString) {
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                cleanupAction.run();
            }
        }, CLEANUP_DELAY_MS);

        if (isString) {
            cancelTimer(props.stringCleanupTimer);
            props.stringCleanupTimer = timer;
        } else {
            cancelTimer(props.boolCleanupTimer);
            props.boolCleanupTimer = timer;
        }
    }

    private static void cancelTimer(Timer timer) {
        if (timer != null) {
            timer.cancel();
        }
    }
}