package com.example.rces.exception;

import com.example.rces.models.enums.NotificationType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class DisabledExceptionBormash extends RuntimeException {

    public DisabledExceptionBormash(String message, NotificationType notificationType) {
        super(message);
        this.notificationType = notificationType;
    }

    private NotificationType notificationType;

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

}
