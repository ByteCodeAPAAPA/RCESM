package com.example.rces.service.impl.telegram.event;

import com.example.rces.models.Employee;
import com.example.rces.service.impl.telegram.MessageType;
import org.springframework.context.ApplicationEvent;

public abstract class TelegramEvent extends ApplicationEvent {
    private final Object entity;
    private final Employee updaterEmployee;
    private final MessageType messageType;

    public TelegramEvent(Object source, Object entity, Employee updaterEmployee, MessageType messageType) {
        super(source);
        this.entity = entity;
        this.updaterEmployee = updaterEmployee;
        this.messageType = messageType;
    }

    public Object getEntity() {
        return entity;
    }

    public Employee getUpdaterEmployee() {
        return updaterEmployee;
    }

    public MessageType getMessageType() {
        return messageType;
    }
}