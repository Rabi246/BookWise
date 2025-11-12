package org.example.bookwise.controller;

import org.example.bookwise.service.GoogleBooksService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class BookSearchController {

    private final GoogleBooksService booksService;

    public BookSearchController(GoogleBooksService booksService) {
        this.booksService = booksService;
    }

    @GetMapping("/booksearch")
    public String bookSearchPage() {
        return "booksearch";
    }

    @ResponseBody
    @GetMapping("/api/searchBooks")
    public String searchBooks(@RequestParam String q) {
        return booksService.searchBooks(q);
    }
}
