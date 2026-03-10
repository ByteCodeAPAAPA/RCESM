package com.example.rces.utils.telegram;

import com.example.rces.models.Requests;

public class ChatIdResolver {
    private final String constructorGroupChatId;
    private final String technologistGroupChatId;

    public ChatIdResolver(String constructorGroupChatId, String technologistGroupChatId) {
        this.constructorGroupChatId = constructorGroupChatId;
        this.technologistGroupChatId = technologistGroupChatId;
    }

    public String resolveGroupId(Requests.Type typeRequest) {
        return switch (typeRequest) {
            case constructor -> constructorGroupChatId;
            case technologist -> technologistGroupChatId;
            default -> null;
        };
    }
}
