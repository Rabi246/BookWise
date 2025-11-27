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
    // 2. MULTIPLE Recommendations (5-book conversational)
    // -----------------------------------------------------------------------------------------
    @ResponseBody
    @PostMapping("/api/ai/recommendations")
    public String getMultipleRecommendations(@RequestParam String query,
                                             @RequestParam(defaultValue = "5") int count) {
        try {
            String prompt = """
                You are a knowledgeable and friendly librarian.

                User request: "%s"

                Respond conversationally (3–6 sentences) with helpful book recommendations.
                You may list the books in a natural way.

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
            """.formatted(query);

            String aiResponse = chatClient.prompt(prompt).call().content();

            // ---------------------------------------------------------
            // STEP 2: Split AI response into conversational text + JSON
            // ---------------------------------------------------------
            int jsonStart = aiResponse.indexOf("{");
            String chatText = aiResponse.substring(0, jsonStart).trim();
            String jsonText = aiResponse.substring(jsonStart).trim();

            // Extract book list from JSON
            List<Map<String, String>> extractedBooks = extractList(jsonText);
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
            String q = "intitle:%s+inauthor:%s".formatted(
                    URLEncoder.encode(title, StandardCharsets.UTF_8),
                    URLEncoder.encode(author, StandardCharsets.UTF_8)
            );

            String url = "https://www.googleapis.com/books/v1/volumes?q=" + q;
            JsonNode json = objectMapper.readTree(restTemplate.getForObject(url, String.class));

            if (!json.has("items")) return null;

            String target = title.toLowerCase();

            // Try to find the closest title match
            for (JsonNode item : json.get("items")) {
                JsonNode info = item.get("volumeInfo");
                if (info == null || !info.has("title")) continue;

                String resultTitle = info.get("title").asText("").toLowerCase();

                if (resultTitle.contains(target)) {
                    return item;
                }
            }

            // Fallback: return first item
            return json.get("items").get(0);

        } catch (Exception e) {
            return null;
        }
    }
}
