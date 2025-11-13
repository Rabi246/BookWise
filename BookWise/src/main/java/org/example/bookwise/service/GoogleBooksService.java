package org.example.bookwise.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GoogleBooksService {

    private final String API_URL = "https://www.googleapis.com/books/v1/volumes";

    public String searchBooks(String query) {
        RestTemplate restTemplate = new RestTemplate();

        String url = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("q", query)
                .toUriString();

        return restTemplate.getForObject(url, String.class);
    }

    public String smartSearch(String query) {
        RestTemplate restTemplate = new RestTemplate();

        // Try in this order: title + author, title only, raw query
        String[] queries = {
                "intitle:\"" + query + "\"",
                "intitle:" + query,
                "inauthor:" + query,
                query.replace("'", ""), // remove apostrophes
                "\"" + query + "\"",     // exact phrase
                query
        };

        for (String q : queries) {
            String url = UriComponentsBuilder.fromHttpUrl(API_URL)
                    .queryParam("q", q)
                    .queryParam("maxResults", 10)
                    .toUriString();

            try {
                String response = restTemplate.getForObject(url, String.class);
                if (response != null && response.contains("items")) {
                    return response;
                }
            } catch (Exception ignored) {}
        }

        return "{\"items\": []}";
    }
}