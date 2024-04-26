package com.example.llama2chatbot;

public class ChatMessage {
    private final String message;
    private final boolean isUser; // true if this message is from the User, false if from the Bot

    public ChatMessage(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }
}
