package org.example.bookwise.controller;

import org.example.bookwise.SessionStore;
import org.example.bookwise.UserService;
import org.example.bookwise.model.Book;
import org.example.bookwise.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
public class BookController {

    private final SessionStore sessionStore;
    private final UserService userService;

    public BookController(SessionStore sessionStore, UserService userService) {
        this.sessionStore = sessionStore;
        this.userService = userService;
    }

    @GetMapping("/mybooks")
    public String myBooks(HttpSession session,Model model) {
        Integer userId = (Integer) session.getAttribute("currentUserId");
        if (userId == null) {
            return "redirect:/login";
        }

        String username = userService.findById(userId)
                .map(User::getUsername)
                .orElse("Guest");

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