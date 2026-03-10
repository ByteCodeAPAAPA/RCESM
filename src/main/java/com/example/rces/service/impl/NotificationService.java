package com.example.rces.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendPrivateNotification(String username, String message, String link, String status) {

        Map<String, String> payload = Map.of(
                "message", message,
                "link", link,
                "status", status
        );

        messagingTemplate.convertAndSendToUser(
                username,
                "queue/notifications",
                payload
        );

    }
}
