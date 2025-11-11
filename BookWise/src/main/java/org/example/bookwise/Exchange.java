package org.example.bookwise;

public class Exchange {
    private String userMessage;
    private String aiMessage;

    public Exchange(String userMessage, String aiResponse) {
        this.userMessage = userMessage;
        this.aiMessage = aiResponse;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getAiMessage() {
        return aiMessage;
    }

    public void setAiMessage(String aiMessage) {
        this.aiMessage = aiMessage;
    }
}
