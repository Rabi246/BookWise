package org.example.bookwise;

import org.example.bookwise.model.User;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

@Component
@SessionScope
public class SessionStore {
    private User currentUser;
    private List<Exchange> chatHistory = new ArrayList<>();

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public List<Exchange> getChatHistory() {
        return chatHistory;
    }

    public void addExchange(Exchange exchange) {
        chatHistory.add(exchange);
    }

    public void clearChatHistory() {
        chatHistory.clear();
    }
}