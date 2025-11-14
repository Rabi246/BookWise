package org.example.bookwise.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Controller
public class BookSearchController {

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * API endpoint for searching Google Books
     * Returns raw JSON response from Google Books API
     */
    @ResponseBody
    @GetMapping("/api/searchBooks")
    public String searchBooks(@RequestParam String q) {
        try {
            String url = "https://www.googleapis.com/books/v1/volumes?q=" +
                    java.net.URLEncoder.encode(q, "UTF-8");

            return restTemplate.getForObject(url, String.class);

        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }
}