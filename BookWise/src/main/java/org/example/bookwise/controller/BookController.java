package org.example.bookwise.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;

@Controller
public class BookController {

    @GetMapping("/mybooks")
    public String myBooks(Model model, Principal principal) {
        String username = (principal != null) ? principal.getName() : null;
        model.addAttribute("username", username);
        return "mybooks";
    }

    @GetMapping("/booksearch")
    public String bookSearch() {
        return "booksearch";
    }
}