package com.example.rces.exception;

import com.example.rces.models.enums.NotificationType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundExceptionBormash extends RuntimeException {

    public EntityNotFoundExceptionBormash(String message, NotificationType notificationType) {
        super(message);
    }

    private NotificationType notificationType;

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }
}
