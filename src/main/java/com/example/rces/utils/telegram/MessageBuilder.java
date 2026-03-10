package com.example.rces.utils.telegram;

import com.example.rces.utils.AppUtil;
import com.example.rces.models.Employee;
import com.example.rces.models.Requests;
import com.example.rces.models.SGI;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.example.rces.utils.DateUtil.formatedDate;

public class MessageBuilder {

    private final String baseUrl;
    private final ChatIdResolver chatIdResolver;

    private static final int TOPIC_CONSTRUCTOR = 2343;

    public MessageBuilder(String baseUrl, ChatIdResolver chatIdResolver) {
        this.baseUrl = baseUrl;
        this.chatIdResolver = chatIdResolver;
    }

    public void buildRequestMessage(Requests request, MessageType type, SendMessage message, Employee updaterEmployee) {
        switch (type) {
            case CREATE -> buildCreateMessage(request, message, updaterEmployee);
            case UPDATE -> buildUpdateMessage(request, message, updaterEmployee);
            case WORK -> buildWorkMessage(request, message, updaterEmployee);
            case REDIRECT -> buildRedirectMessage(request, message, updaterEmployee);
            case COMPLETED -> buildCompletedMessage(request, message);
            case COMPLETED_WORK -> buildCompletedWorkMessage(request, message, type);
            case CANCEL -> buildCancelMessage(request, message, updaterEmployee);
            case CLOSE -> buildCloseMessage(request, message, updaterEmployee);
            case REJECTED -> buildRejectedMessage(request, message, updaterEmployee);
            default -> throw new IllegalArgumentException("Не валидный тип сообщения Request");
        }
    }

    public void buildRequestMessage(SGI sgi, MessageType type, SendMessage message) {
        switch (type) {
            case CREATE -> buildCreateMessage(sgi, message);
            case WORK -> buildWorkMessage(sgi, message);
            case UPDATE -> buildUpdateMessage(sgi, message);
            case COMPLETED -> buildCompleteMessage(sgi, message);
            case CLOSE -> buildCloseMessage(sgi, message);
            case DELETE -> buildDeleteMessage(sgi, message);
            case REGULAR -> buildRegularMessage(message);
            default -> throw new IllegalArgumentException("Не валидный тип сообщения SGI");
        }
    }

    private void buildCreateMessage(Requests request, SendMessage message, Employee updaterEmployee) {
        String reason;
        if (updaterEmployee != null) {
            if (updaterEmployee.getRole().equals("CONSTRUCTOR")) {
                message.setMessageThreadId(TOPIC_CONSTRUCTOR);
                message.setChatId(chatIdResolver.resolveGroupId(request.getTypeRequest()));
            } else {
                message.setChatId(updaterEmployee.getChatId());
            }
        } else {
            message.setChatId(chatIdResolver.resolveGroupId(request.getTypeRequest()));
        }
        if (request.getTypeRequest().equals(Requests.Type.otk)) {
            reason = request.getReason_wr() != null ? request.getReason_wr() : "Причина не указана";
        } else {
            reason = request.getReason() != null ? request.getReason().getName() : "Причина не указана";
        }
        message.setText(
                String.format(
                        "Создана новая заявка: %d\nОтветственный: %s%s\nЗаказ клиента: %s%s\n%s\nКомментарий: %s\nПричина: %s\nСсылка на заявку: %s",
                        request.getRequestNumber(),
                        request.getEmployee() != null ? request.getEmployee().getName() : "Пользователь не задан",
                        request.getSubDivision() != null ? "\nЦех: " + request.getSubDivision().getName() : "Цех не задан",
                        request.getCustomerOrder() != null ? request.getCustomerOrder().getName() : "Заказ не указан",
                        (request.getItem() != null && !"ОГТ".equals(request.getTypeRequest().name())) ? "\nТип ТМЦ: " + request.getItem().getName() : "",
                        !request.getImages().isEmpty() ? "Прикреплены фото" : "Фото не прикреплены",
                        request.getComment() != null ? request.getComment() : "Комментарий не прикреплен",
                        reason,
                        baseUrl + "/view/" + request.getRequestNumber()));
    }

    private void buildUpdateMessage(Requests request, SendMessage message, Employee updaterEmployee) {
        String reason;
        if (updaterEmployee.getRole().equals("CONSTRUCTOR")) {
            message.setMessageThreadId(TOPIC_CONSTRUCTOR);
            message.setChatId(chatIdResolver.resolveGroupId(request.getTypeRequest()));
        } else {
            message.setChatId(updaterEmployee.getChatId());
        }
        if (request.getTypeRequest().equals(Requests.Type.otk)) {
            reason = request.getReason_wr() != null ? request.getReason_wr() : "Причина не указана";
        } else {
            reason = request.getReason() != null ? request.getReason().getName() : "Причина не указана";
        }
        message.setText(
                String.format(
                        "Заявка обновлена: %d \nОтветственный: %s \n%s\nЗаказ клиента: %s\nТип ТМЦ: %s \n%s\nКомментарий: %s\nПричина: %s\nСтатус: %s\nСсылка на заявку: %s",
                        request.getRequestNumber(),
                        request.getEmployee().getName(),
                        request.getSubDivision().getName(),
                        request.getCustomerOrder() != null ? request.getCustomerOrder().getName() : "не указан ",
                        request.getItem() != null && "ОГТ".equals(request.getTypeRequest().getName()) ? "\nТип ТМЦ: " + request.getItem().getName() : "",
                        !request.getImages().isEmpty() ? "Прикреплены фото" : "Фото не прикреплены",
                        request.getComment() != null ? request.getComment() : "",
                        reason,
                        request.getStatus().getName(),
                        baseUrl + "/view/" + request.getRequestNumber()));
    }

    private void buildWorkMessage(Requests request, SendMessage message, Employee updaterEmployee) {
        message.setChatId(updaterEmployee.getChatId());
        message.setText(String.format("Заявка №%d взята в работу", request.getRequestNumber()));
    }

    private void buildRedirectMessage(Requests request, SendMessage message, Employee updaterEmployee) {
        if (updaterEmployee.getRole().equals("CONSTRUCTOR")) {
            message.setMessageThreadId(TOPIC_CONSTRUCTOR);
            message.setChatId(chatIdResolver.resolveGroupId(request.getTypeRequest()));
        } else {
            message.setChatId(updaterEmployee.getChatId());
        }
        message.setText(
                String.format(
                        "%s переадресовал заявку %d в вашу ответственность \nОтветственный: %s%s\nЗаказ клиента: %s%s\n%s\nКомментарий: %s\nПричина: %s\nСсылка на заявку: %s",
                        request.getUpdatedBy().getName(),
                        request.getRequestNumber(),
                        request.getEmployee().getName(),
                        request.getSubDivision() != null ? "\nЦех: " + request.getSubDivision().getName() : "",
                        request.getCustomerOrder().getName(),
                        request.getItem() != null ? "\nТип ТМЦ: " + request.getItem().getName() : "\nТип не задан",
                        !request.getImages().isEmpty() ? "Прикреплены фото" : "Фото не прикреплены",
                        request.getComment() != null ? request.getComment() : "",
                        request.getReason() != null ? request.getReason().getName() : "Причина не указана",
                        baseUrl + "/view/" + request.getRequestNumber()));
    }

    private void buildCompletedMessage(Requests request, SendMessage message) {
        message.setChatId(request.getCreatedBy().getChatId());
        message.setText(
                String.format(
                        "Заявка №%d Выполнена\nСсылка на заявку \n%s/view/%d\n" +
                                (!request.getDescription().isEmpty() ? "Описание решения: %s" : ""),
                        request.getRequestNumber(), baseUrl, request.getRequestNumber(), request.getDescription()));
    }

    private void buildCompletedWorkMessage(Requests request, SendMessage message, MessageType messageType) {
        message.setChatId(request.getEmployee().getChatId());
        switch (messageType) {
            case CLOSE -> message.setText(
                    String.format(
                            "Заявка № %d закрыта\n" + (!request.getDescription().isEmpty() ? "Описание: %s" : ""),
                            request.getRequestNumber(), request.getDescription()));
            case CANCEL -> message.setText(
                    String.format("Заявка № %d забракована\n"
                                    + (!request.getDescription().isEmpty() ? "Описание: %s" : ""),
                            request.getRequestNumber(), request.getDescription())
            );
        }
    }

    private void buildCancelMessage(Requests request, SendMessage message, Employee updaterEmployee) {
        message.setChatId(updaterEmployee.getChatId());
        message.setText(
                String.format("Заявка № %d забракована\n",
                        request.getRequestNumber()));
    }

    private void buildRejectedMessage(Requests requests, SendMessage message, Employee createEmployee) {
        message.setChatId(createEmployee.getChatId());
        message.setText(
                String.format("Заявка № %d не прошла ОТК в количестве: %d", requests.getRequestNumber(), requests.getQty())
        );
    }

    private void buildCloseMessage(Requests request, SendMessage message, Employee updaterEmployee) {
        message.setChatId(updaterEmployee.getChatId());
        message.setText(
                String.format("Заявка № %d закрыта", request.getRequestNumber()));
    }

    private void buildCreateMessage(SGI sgi, SendMessage message) {
        message.setText(String.format("Новое мероприятие №%d\nМероприятие: %s\nОтветственный: %s\nЖелаемый срок: %s\nСопутствующие действия: %s\nПримечание: %s",
                sgi.getRequestNumber(), sgi.getEvent(), sgi.getEmployee().getName(), formatedDate(sgi.getDesiredDate()), sgi.getActions(), sgi.getNote() != null ? sgi.getNote() : ""));
    }

    private void buildWorkMessage(SGI sgi, SendMessage message) {
        message.setText(String.format("На мероприятие №%d установлена плановая дата - %s", sgi.getRequestNumber(), sgi.getPlanDate()));
    }

    private void buildUpdateMessage(SGI sgi, SendMessage message) {
        message.setText(String.format("Мероприятие №%d обновлено", sgi.getRequestNumber()));
    }

    private void buildCompleteMessage(SGI sgi, SendMessage message) {
        message.setText(String.format("Мероприятие №%d выполнено", sgi.getRequestNumber()));
    }

    private void buildCloseMessage(SGI sgi, SendMessage message) {
        message.setText(sgi.getAgreed() ? String.format("Мероприятие №%d закрыто", sgi.getRequestNumber()) : String.format("Мероприятие №%s снова открыто", sgi.getRequestNumber()));
    }

    private void buildDeleteMessage(SGI sgi, SendMessage message) {
        message.setText(String.format("Мероприятие №%d удалено", sgi.getRequestNumber()));
    }

    private void buildRegularMessage(SendMessage message) {
        message.setText(String.format("Просрочен срок выполнения мероприятий: №%s", AppUtil.getString()));
    }
}