package org.example.bookwise.controller;

import org.example.bookwise.SessionStore;
import org.example.bookwise.model.Book;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class BookController {

    private final SessionStore sessionStore;

    public BookController(SessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    @GetMapping("/mybooks")
    public String myBooks(Model model, Principal principal) {
        String username = (principal != null) ? principal.getName() : null;

        if (sessionStore.getCurrentUser() != null) {
            username = sessionStore.getCurrentUser().getUsername();
        }

        model.addAttribute("username", username);
        model.addAttribute("books", sessionStore.getMyBooks());
        return "mybooks";
    }

    @GetMapping("/booksearch")
    public String bookSearch() {
        return "booksearch";
    }

    /**
     * Add a book to user's library
     */
    @PostMapping("/api/addBook")
    @ResponseBody
    public String addBook(@RequestBody Book book) {
        try {
            sessionStore.addBook(book);
            return "{\"success\": true, \"message\": \"Book added to library\"}";
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
        }
    }

    /**
     * Remove a book from user's library
     */
    @PostMapping("/api/removeBook")
    @ResponseBody
    public String removeBook(@RequestParam String bookId) {
        try {
            sessionStore.removeBook(bookId);
            return "{\"success\": true, \"message\": \"Book removed from library\"}";
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
        }
    }

    /**
     * Check if a book is in user's library
     */
    @GetMapping("/api/hasBook")
    @ResponseBody
    public String hasBook(@RequestParam String bookId) {
        boolean has = sessionStore.hasBook(bookId);
        return "{\"hasBook\": " + has + "}";
    }
}