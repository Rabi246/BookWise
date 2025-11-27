package org.example.bookwise.controller;

import org.example.bookwise.SessionStore;
import org.example.bookwise.UserService;
import org.example.bookwise.model.Book;
import org.example.bookwise.model.Library;
import org.example.bookwise.model.Rating;
import org.example.bookwise.model.User;
import org.example.bookwise.repository.LibraryRepository;
import org.example.bookwise.repository.BookRepository;
import org.example.bookwise.repository.RatingRepository;
import org.example.bookwise.service.AiBookSummaryService;
import org.example.bookwise.service.AiRecommendationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
public class BookController {
    private final UserService userService;
    private final LibraryRepository libraryRepository;
    private final BookRepository bookRepository;
    private final RatingRepository ratingRepository;
    private final AiBookSummaryService aiBookSummaryService;
    private final AiRecommendationService aiRecommendationService;


    public BookController(UserService userService, LibraryRepository libraryRepository, BookRepository bookRepository,
                          RatingRepository ratingRepository, AiBookSummaryService aiBookSummaryService,
                          AiRecommendationService aiRecommendationService) {
        this.aiRecommendationService = aiRecommendationService;
        this.aiBookSummaryService = aiBookSummaryService;
        this.ratingRepository = ratingRepository;
        this.userService = userService;
        this.libraryRepository = libraryRepository;
        this.bookRepository = bookRepository;
    }

    @GetMapping("/mybooks")
    public String myBooks(HttpSession session,Model model) {
        Integer userId = (Integer) session.getAttribute("currentUserId");
        if (userId == null) {
            return "redirect:/login";
        }

        User user = userService.findById(userId).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", user.getUsername());
        model.addAttribute("entries", libraryRepository.findByOwner(user));

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
    public String addBookToDB(@RequestBody Book requestBook, HttpSession session) {

        Integer userId = (Integer) session.getAttribute("currentUserId");
        if (userId == null) {
            return "{\"success\": false, \"message\": \"Not logged in\"}";
        }

        User user = userService.findById(userId).orElse(null);
        if (user == null) {
            return "{\"success\": false, \"message\": \"Invalid user\"}";
        }

        // Save or find existing book
        Book book = bookRepository.findById(requestBook.getId())
                .orElse(requestBook);

        Book saved = bookRepository.save(book);

        // Create library entry
        Library lib = new Library();
        lib.setOwner(user);
        lib.setBook(saved);

        libraryRepository.save(lib);

        return "{\"success\": true, \"message\": \"Book added to library\"}";
    }

    /**
     * Remove a book from user's library
     */
    @PostMapping("/api/removeBook")
    @ResponseBody
    public String removeBook(@RequestParam String bookId, HttpSession session) {

        Integer userId = (Integer) session.getAttribute("currentUserId");
        if (userId == null) {
            return "{\"success\": false, \"message\": \"Not logged in\"}";
        }

        User user = userService.findById(userId).orElse(null);
        if (user == null) {
            return "{\"success\": false, \"message\": \"Invalid user\"}";
        }

        // Find the library entry with THIS user + THIS book
        Library entry = libraryRepository.findByOwner(user)
                .stream()
                .filter(e -> e.getBook().getId().equals(bookId))
                .findFirst()
                .orElse(null);

        if (entry == null) {
            return "{\"success\": false, \"message\": \"Book not found in library\"}";
        }

        libraryRepository.delete(entry);

        return "{\"success\": true, \"message\": \"Book removed\"}";
    }

    /**
     * Check if a book is in user's library
     */
    @GetMapping("/api/hasBook")
    @ResponseBody
    public String hasBook(@RequestParam String bookId, HttpSession session) {

        Integer userId = (Integer) session.getAttribute("currentUserId");
        if (userId == null) {
            return "{\"hasBook\": false}";
        }

        User user = userService.findById(userId).orElse(null);
        if (user == null) {
            return "{\"hasBook\": false}";
        }

        boolean has = libraryRepository.findByOwner(user)
                .stream()
                .anyMatch(entry -> entry.getBook().getId().equals(bookId));

        return "{\"hasBook\": " + has + "}";
    }

    @PostMapping("/api/rateBook")
    @ResponseBody
    public String rateBook(@RequestParam String bookId,
                           @RequestParam int value,
                           HttpSession session) {

        Integer userId = (Integer) session.getAttribute("currentUserId");
        if (userId == null) {
            return "{\"success\": false, \"message\": \"Not logged in\"}";
        }

        User user = userService.findById(userId).orElse(null);
        if (user == null) {
            return "{\"success\": false, \"message\": \"User not found\"}";
        }

        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            return "{\"success\": false, \"message\": \"Book not found\"}";
        }

        // Check if rating exists
        Rating rating = ratingRepository.findByUserAndBook(user, book)
                .orElse(new Rating(user, book, value));

        rating.setValue(value);
        ratingRepository.save(rating);

        return "{\"success\": true}";
    }

    @GetMapping("/api/getRating")
    @ResponseBody
    public String getRating(@RequestParam String bookId, HttpSession session) {

        Integer userId = (Integer) session.getAttribute("currentUserId");
        if (userId == null) {
            return "{\"rating\": 0}";
        }

        User user = userService.findById(userId).orElse(null);
        if (user == null) {
            return "{\"rating\": 0}";
        }

        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            return "{\"rating\": 0}";
        }

        Rating rating = ratingRepository.findByUserAndBook(user, book)
                .orElse(null);

        if (rating == null) {
            return "{\"rating\": 0}";
        }

        return "{\"rating\": " + rating.getValue() + "}";
    }
    /**
     * Generate AI summary for a book
     */
    @GetMapping("/api/summary")
    @ResponseBody
    public String getSummary(@RequestParam String bookId) {

        Book book = bookRepository.findById(bookId).orElse(null);

        if (book == null) {
            return "{\"success\": false, \"message\": \"Book not found\"}";
        }

        String summary = aiBookSummaryService.generateSummary(
                book.getTitle(),
                book.getAuthors(),
                book.getDescription()
        );

        return """
        {
            "success": true,
            "summary": "%s"
        }
        """.formatted(summary.replace("\"", "'").replace("\n", "\\n"));
    }

    /**
     * Generate AI character relationship chart for a book
     */
    @GetMapping("/api/relationships")
    @ResponseBody
    public String generateRelationships(@RequestParam String bookId, HttpSession session) {

        Integer userId = (Integer) session.getAttribute("currentUserId");
        if (userId == null) return "{\"success\": false, \"message\":\"Not logged in\"}";

        var user = userService.findById(userId).orElse(null);
        if (user == null) return "{\"success\": false, \"message\":\"Invalid user\"}";

        var book = bookRepository.findById(bookId).orElse(null);
        if (book == null) return "{\"success\": false, \"message\":\"Book not found\"}";

        String chart = aiBookSummaryService.generateRelationshipChart(
                book.getTitle(),
                book.getAuthors(),
                book.getDescription()
        );

        String safe = chart.replace("\"", "\\\"").replace("\n", "\\n");

        return "{\"success\": true, \"chart\": \"" + safe + "\"}";
    }
    @GetMapping("/api/recommend")
    @ResponseBody
    public String recommend(HttpSession session) {

        Integer userId = (Integer) session.getAttribute("currentUserId");
        if (userId == null) return "{\"success\": false, \"message\": \"Not logged in\"}";

        User user = userService.findById(userId).orElse(null);
        if (user == null) return "{\"success\": false}";

        var entries = libraryRepository.findByOwner(user);

        String recs = aiRecommendationService.generateRecommendations(user, entries);

        return """
        {
            "success": true,
            "recommendations": "%s"
        }
        """.formatted(
                recs.replace("\"", "'")
                        .replace("\n", "\\n")
        );
    }



}