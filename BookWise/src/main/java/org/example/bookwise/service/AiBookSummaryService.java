package org.example.bookwise.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
public class AiBookSummaryService {

    private final ChatClient chatClient;

    public AiBookSummaryService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String generateSummary(String title, String author, String description) {
        String prompt = """
            Create a spoiler-free analysis for the book:

            Title: %s
            Author: %s

            Description from Google Books:
            "%s"

            Provide the following:

            1. A short 3–5 sentence summary
            2. Key themes
            3. Main characters
            4. Why someone might enjoy this book
            5. 3–5 similar books

            Format the *entire output in clean HTML*.
            Use <h2>, <h3>, <p>, <ul>, <li>, and <strong>.
            Do **not** use Markdown.
            """.formatted(title, author, description == null ? "" : description);

        Prompt p = new Prompt(new UserMessage(prompt));
        return chatClient.prompt(p).call().content();
    }
}
