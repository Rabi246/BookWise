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
}