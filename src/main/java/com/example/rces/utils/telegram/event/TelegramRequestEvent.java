package com.example.rces.utils.telegram.event;

import com.example.rces.models.Employee;
import com.example.rces.models.Requests;
import com.example.rces.utils.telegram.MessageType;

public class TelegramRequestEvent extends TelegramEvent {

    private final Requests request;

    public TelegramRequestEvent(Object source, Requests request, Employee updaterEmployee, MessageType messageType) {
        super(source, request, updaterEmployee, messageType);
        this.request = request;
    }

    public Requests getRequest() {
        return request;
    }

    @Override
    public Requests getEntity() {
        return request;
    }
}
