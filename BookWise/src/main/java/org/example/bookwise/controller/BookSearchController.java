package org.example.bookwise.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import java.net.URI;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class BookSearchController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${google.books.api-key:}")
    private String googleApiKey;

    /**
     * Searches Google Books on the server so the API key stays private.
     */
    @ResponseBody
    @GetMapping("/api/searchBooks")
    public String searchBooks(@RequestParam String q) {
        try {
            String url = "https://www.googleapis.com/books/v1/volumes?q="
                    + URLEncoder.encode(q, StandardCharsets.UTF_8)
                    + "&maxResults=20";
            if (!googleApiKey.isBlank()) {
                url += "&key=" + googleApiKey;
            }
            return restTemplate.getForObject(URI.create(url), String.class);

        } catch (Exception e) {
            System.out.println("[GoogleBooks] Search failed for '" + q + "': " + e.getMessage());
            return "{\"error\": \"Search failed\"}";
        }
    }
}