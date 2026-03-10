package com.example.rces.service.impl.telegram.event;

import com.example.rces.models.Employee;
import com.example.rces.models.SGI;
import com.example.rces.service.impl.telegram.MessageType;

public class TelegramSgiEvent extends TelegramEvent {

    private final SGI sgi;
    private final String chatId;

    public TelegramSgiEvent(Object source, SGI sgi, Employee updaterEmployee, MessageType messageType, String chatId) {
        super(source, sgi, updaterEmployee, messageType);
        this.sgi = sgi;
        this.chatId = chatId;
    }

    public SGI getSgi() {
        return sgi;
    }

    @Override
    public SGI getEntity() {
        return sgi;
    }

    public String getChatId() {
        return chatId;
    }
}
