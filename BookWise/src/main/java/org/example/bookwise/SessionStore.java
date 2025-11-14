package org.example.bookwise;

import org.example.bookwise.model.Book;
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
    private List<Book> myBooks = new ArrayList<>();

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

    public List<Book> getMyBooks() {
        return myBooks;
    }

    public void addBook(Book book) {
        // Check if book already exists (by ID)
        boolean exists = myBooks.stream()
                .anyMatch(b -> b.getId().equals(book.getId()));

        if (!exists) {
            myBooks.add(book);
        }
    }

    public void removeBook(String bookId) {
        myBooks.removeIf(book -> book.getId().equals(bookId));
    }

    public boolean hasBook(String bookId) {
        return myBooks.stream()
                .anyMatch(book -> book.getId().equals(bookId));
    }
}