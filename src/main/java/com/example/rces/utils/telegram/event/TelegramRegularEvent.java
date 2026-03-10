package com.example.rces.utils.telegram.event;

import com.example.rces.utils.telegram.MessageType;

public class TelegramRegularEvent extends TelegramEvent {
    private final String message;
    private final String chatId;

    public TelegramRegularEvent(Object source, String message, String chatId) {
        super(source, null, null, MessageType.REGULAR);
        this.message = message;
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public String getChatId() {
        return chatId;
    }
}
