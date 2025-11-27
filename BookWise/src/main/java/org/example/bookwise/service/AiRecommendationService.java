package org.example.bookwise.service;


import org.example.bookwise.model.Book;
import org.example.bookwise.model.Library;
import org.example.bookwise.model.User;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiRecommendationService {

    private final ChatClient chatClient;

    public AiRecommendationService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String generateRecommendations(User user, List<Library> entries) {

        StringBuilder bookList = new StringBuilder();
        for (Library e : entries) {
            Book b = e.getBook();
            bookList.append("""
                Title: %s
                Author: %s
                Description: %s
                Rating: unknown

            """.formatted(
                    b.getTitle(),
                    b.getAuthors(),
                    b.getDescription()
            ));
        }

        String prompt = """
            You are a personalized AI book recommender.

            User’s reading history:

            %s

            Based on the user's behavior, preferences, themes, ratings,
            and patterns, create 5–7 book recommendations with:

            1. Title + Author
            2. 2–4 sentence explanation
            3. Why this matches the user’s taste
            4. Confidence score (1-100)
            5. Similar books

            Format the *entire output in clean HTML*.
            Use <h2>, <h3>, <p>, <ul>, <li>,<table> and <strong>.
            Do **not** use Markdown.
            """.formatted(bookList);

        var response = chatClient.prompt(new Prompt(new UserMessage(prompt)))
                .call()
                .chatResponse();

// extract text safely
        String output = response.getResults()
                .get(0)
                .getOutput()
                .getText();

        return output;

    }
}


