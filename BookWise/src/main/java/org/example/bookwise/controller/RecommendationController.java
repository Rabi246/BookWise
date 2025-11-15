package org.example.bookwise.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class RecommendationController {

    private final ChatClient chatClient;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RecommendationController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Get ONE book recommendation with conversational response
     */
    @ResponseBody
    @PostMapping("/api/ai/recommendation")
    public String getOneRecommendation(@RequestParam String query) {
        try {
            // Ask AI for conversational recommendation
            String prompt = String.format("""
                You are a knowledgeable and friendly librarian helping someone find a book.
                
                User's request: "%s"
                
                Respond conversationally (2-3 sentences) explaining:
                1. Why you're recommending this specific book
                2. What makes it special or relevant to their interest
                
                Format your recommendation like this:
                "I think you'd really enjoy **Title** by Author Name! [Your explanation here]"
                
                Use **bold** for the book title. Be warm and enthusiastic!
                """, query);

            String aiResponse = chatClient.prompt(prompt).call().content();

            // Extract title and author from AI response
            Map<String, String> bookInfo = extractBookInfo(aiResponse);

            if (bookInfo != null) {
                // Search Google Books to verify and get full details
                String googleResult = searchGoogleBooks(bookInfo.get("title"), bookInfo.get("author"));
                JsonNode resultJson = objectMapper.readTree(googleResult);

                // Combine AI explanation with Google Books data
                Map<String, Object> response = new HashMap<>();
                response.put("aiResponse", aiResponse);
                response.put("books", resultJson.get("items"));

                return objectMapper.writeValueAsString(response);
            }

            return "{\"error\": \"Could not extract book information\"}";

        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    /**
     * Get MULTIPLE book recommendations with conversational response
     */
    @ResponseBody
    @PostMapping("/api/ai/recommendations")
    public String getMultipleRecommendations(@RequestParam String query,
                                             @RequestParam(defaultValue = "5") int count) {
        try {
            // Ask AI for conversational recommendations
            String prompt = String.format("""
                You are a knowledgeable and friendly librarian helping someone find books.
                
                User's request: "%s"
                
                Recommend %d books with a warm, conversational tone. For each book:
                1. Use format: **Title** by Author Name
                2. Explain in 1-2 sentences why you recommend it
                3. Number your recommendations (1., 2., 3., etc.)
                
                Example format:
                "I have some wonderful recommendations for you!
                
                1. **Pride and Prejudice** by Jane Austen – This is a timeless romance with witty dialogue and unforgettable characters. Elizabeth and Darcy's relationship develops beautifully throughout.
                
                2. **The Notebook** by Nicholas Sparks – A deeply emotional love story that spans decades and will have you reaching for tissues!"
                
                Be enthusiastic and personal, like you're talking to a friend!
                """, query, count);

            String aiResponse = chatClient.prompt(prompt).call().content();

            // Extract all books from AI response
            List<Map<String, String>> extractedBooks = extractAllBooks(aiResponse);
            List<JsonNode> verifiedBooks = new ArrayList<>();

            // Verify each book with Google Books
            for (Map<String, String> bookInfo : extractedBooks) {
                String googleResult = searchGoogleBooks(bookInfo.get("title"), bookInfo.get("author"));
                JsonNode resultJson = objectMapper.readTree(googleResult);

                if (resultJson.has("items") && resultJson.get("items").size() > 0) {
                    verifiedBooks.add(resultJson.get("items").get(0));
                }
            }

            // Combine AI explanation with Google Books data
            Map<String, Object> response = new HashMap<>();
            response.put("aiResponse", aiResponse);
            response.put("items", verifiedBooks);

            return objectMapper.writeValueAsString(response);

        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    /**
     * Extract book title and author from conversational text
     */
    private Map<String, String> extractBookInfo(String text) {
        // Pattern: **Title** by Author
        Pattern pattern = Pattern.compile("\\*\\*([^*]+)\\*\\*\\s*by\\s+([^.!?\n]+)");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            Map<String, String> book = new HashMap<>();
            book.put("title", matcher.group(1).trim());
            book.put("author", matcher.group(2).trim());
            return book;
        }
        return null;
    }

    /**
     * Extract ALL books from conversational text
     */
    private List<Map<String, String>> extractAllBooks(String text) {
        List<Map<String, String>> books = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\*\\*([^*]+)\\*\\*\\s*by\\s+([^.!?\n]+)");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            Map<String, String> book = new HashMap<>();
            book.put("title", matcher.group(1).trim());
            book.put("author", matcher.group(2).trim());
            books.add(book);
        }

        return books;
    }

    /**
     * Helper: Search Google Books API for a specific title and author
     */
    private String searchGoogleBooks(String title, String author) {
        try {
            String query = String.format("intitle:%s inauthor:%s",
                    java.net.URLEncoder.encode(title, "UTF-8"),
                    java.net.URLEncoder.encode(author, "UTF-8"));

            String url = "https://www.googleapis.com/books/v1/volumes?q=" + query;
            return restTemplate.getForObject(url, String.class);

        } catch (Exception e) {
            return "{\"items\": []}";
        }
    }
}