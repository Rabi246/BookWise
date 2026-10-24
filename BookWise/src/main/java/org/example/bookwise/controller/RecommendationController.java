package org.example.bookwise.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
public class RecommendationController {

    private final ChatClient chatClient;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${google.books.api-key:}")
    private String googleApiKey;

    public RecommendationController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    // -----------------------------------------------------------------------------------------
    // 1. ONE Recommendation (conversational)
    // -----------------------------------------------------------------------------------------
    @ResponseBody
    @PostMapping("/api/ai/recommendation")
    public String getOneRecommendation(@RequestParam String query) {
        try {
            String prompt = """
                You are a knowledgeable and friendly librarian.

                User request: "%s"

                Respond conversationally (2–3 sentences). Recommend ONE book ONLY.

                Your response *must contain* EXACTLY ONE JSON object at the end:

                {
                    "title": "Book Title",
                    "author": "Author Name"
                }

                Do NOT use markdown formatting here.
                """.formatted(query);

            String aiResponse = chatClient.prompt(prompt).call().content();

            Map<String, String> bookInfo = extractSingle(aiResponse);
            if (bookInfo == null) return "{\"error\": \"AI did not return valid JSON.\"}";

            // Verify via Google Books
            JsonNode googleBook = verifyGoogleBook(bookInfo.get("title"), bookInfo.get("author"));

            Map<String, Object> response = new HashMap<>();
            response.put("aiResponse", aiResponse);
            response.put("book", googleBook);

            return objectMapper.writeValueAsString(response);

        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    // -----------------------------------------------------------------------------------------
    // 2. MULTIPLE Recommendations (5-book conversational)
    // -----------------------------------------------------------------------------------------
    @ResponseBody
    @PostMapping("/api/ai/recommendations")
    public String getMultipleRecommendations(@RequestParam String query,
                                             @RequestParam(defaultValue = "5") int count) {
        try {
            String prompt = """
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
                make sure the conversation is in html formatetted with <b>,<br>,<lu>.

                IMPORTANT:
                After your conversational reply, output EXACTLY ONE JSON object on the last line ONLY.
                The JSON must be:

                {
                   "books": [
                      { "title": "...", "author": "..." },
                      { "title": "...", "author": "..." }
                   ]
                }

                The conversational text must come FIRST.
                The JSON must come LAST and must be valid.
            """.formatted(query,count);

            String aiResponse = chatClient.prompt(prompt).call().content();

            // ---------------------------------------------------------
            // STEP 2: Split AI response into conversational text + JSON
            // ---------------------------------------------------------
            int jsonStart = aiResponse.indexOf("{");
            String chatText = aiResponse.substring(0, jsonStart).trim();
            String jsonText = aiResponse.substring(jsonStart).trim();

            // Extract book list from JSON
            List<Map<String, String>> extractedBooks = extractList(jsonText);
            System.out.println("[AI] Parsed " + extractedBooks.size() + " books from AI response");
            List<JsonNode> verifiedBooks = new ArrayList<>();

            // Verify each recommendation using Google Books
            for (Map<String, String> bookInfo : extractedBooks) {
                JsonNode verified = verifyGoogleBook(bookInfo.get("title"), bookInfo.get("author"));
                if (verified != null) {
                    verifiedBooks.add(verified);
                }
            }

            // Final combined response
            Map<String, Object> response = new HashMap<>();
            response.put("message", chatText);     // LEFT side (chat)
            response.put("items", verifiedBooks);  // RIGHT side (recommendation cards)

            return objectMapper.writeValueAsString(response);

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    // -----------------------------------------------------------------------------------------
    // JSON Extraction: Single book
    // -----------------------------------------------------------------------------------------
    private Map<String, String> extractSingle(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            if (!root.has("title") || !root.has("author")) return null;

            Map<String, String> map = new HashMap<>();
            map.put("title", root.get("title").asText());
            map.put("author", root.get("author").asText());
            return map;
        } catch (Exception e) {
            return null;
        }
    }

    // -----------------------------------------------------------------------------------------
    // JSON Extraction: List of books
    // -----------------------------------------------------------------------------------------
    private List<Map<String, String>> extractList(String json) {
        List<Map<String, String>> list = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(json);
            if (!root.has("books")) return list;

            for (JsonNode b : root.get("books")) {
                Map<String, String> m = new HashMap<>();
                m.put("title", b.get("title").asText());
                m.put("author", b.get("author").asText());
                list.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // -----------------------------------------------------------------------------------------
    // Google Books Verification — best-match selection
    // -----------------------------------------------------------------------------------------
    private JsonNode verifyGoogleBook(String title, String author) {
        try {
            // Search by title, with the first author as a loose hint (not a strict filter)
            String firstAuthor = author.split(",| and ")[0].trim();
            String q = "intitle:" + title + " " + firstAuthor;

            String url = "https://www.googleapis.com/books/v1/volumes?q="
                    + URLEncoder.encode(q, StandardCharsets.UTF_8)
                    + "&country=US&maxResults=10";
            if (!googleApiKey.isBlank()) {
                url += "&key=" + googleApiKey;
            }

            // URI.create stops RestTemplate from encoding the URL a second time
            JsonNode json = objectMapper.readTree(restTemplate.getForObject(URI.create(url), String.class));

            if (!json.has("items")) {
                System.out.println("[GoogleBooks] No results for: " + title + " by " + author
                        + " (totalItems=" + json.path("totalItems").asInt() + ")");
                return null;
            }

            String target = title.toLowerCase();
            for (JsonNode item : json.get("items")) {
                JsonNode info = item.get("volumeInfo");
                if (info == null || !info.has("title")) continue;
                if (info.get("title").asText("").toLowerCase().contains(target)) {
                    return item;
                }
            }
            return json.get("items").get(0);

        } catch (Exception e) {
            System.out.println("[GoogleBooks] Lookup failed for '" + title + "': " + e.getMessage());
            return null;
        }
    }
}
