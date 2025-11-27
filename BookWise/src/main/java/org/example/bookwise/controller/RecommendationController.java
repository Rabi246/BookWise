package org.example.bookwise.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
public class RecommendationController {

    private final ChatClient chatClient;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

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
    // 2. MULTIPLE Recommendations (the 5-volume one you're using)
    // -----------------------------------------------------------------------------------------
    @ResponseBody
    @PostMapping("/api/ai/recommendations")
    public String getMultipleRecommendations(@RequestParam String query,
                                             @RequestParam(defaultValue = "5") int count) {
        try {
            String prompt = """
                You are a librarian. Return EXACTLY this JSON format:

                {
                  "books": [
                    { "title": "TITLE_1", "author": "AUTHOR_1" },
                    { "title": "TITLE_2", "author": "AUTHOR_2" }
                  ]
                }

                NO explanations.
                NO markdown.
                NO extra sentences.

                User request: "%s"
                """.formatted(query);

            String aiResponse = chatClient.prompt(prompt).call().content();

            // Extract AI JSON list
            List<Map<String, String>> extractedBooks = extractList(aiResponse);
            List<JsonNode> verifiedBooks = new ArrayList<>();

            // -----------------------------------------------------------------------
            // 💛 FIXED VERIFICATION LOOP — ensures correct volume match
            // -----------------------------------------------------------------------
            for (Map<String, String> bookInfo : extractedBooks) {
                JsonNode verified = verifyGoogleBook(bookInfo.get("title"), bookInfo.get("author"));
                if (verified != null) {
                    verifiedBooks.add(verified);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("aiResponse", aiResponse);
            response.put("items", verifiedBooks);

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
    // Google Books Verification — volume-aware exact matching
    // -----------------------------------------------------------------------------------------
    private JsonNode verifyGoogleBook(String title, String author) {
        try {
            String q = "intitle:%s+inauthor:%s".formatted(
                    URLEncoder.encode(title, StandardCharsets.UTF_8),
                    URLEncoder.encode(author, StandardCharsets.UTF_8)
            );

            String url = "https://www.googleapis.com/books/v1/volumes?q=" + q;
            JsonNode json = objectMapper.readTree(restTemplate.getForObject(url, String.class));

            if (!json.has("items")) return null;

            String target = title.toLowerCase();

            // ✅ Try to find the exact volume match
            for (JsonNode item : json.get("items")) {
                JsonNode info = item.get("volumeInfo");
                if (info == null || !info.has("title")) continue;

                String resultTitle = info.get("title").asText("").toLowerCase();

                if (resultTitle.contains(target)) {
                    return item;
                }
            }

            // Fallback: return first result
            return json.get("items").get(0);

        } catch (Exception e) {
            return null;
        }
    }
}
