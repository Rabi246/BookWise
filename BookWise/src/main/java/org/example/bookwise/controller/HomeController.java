package org.example.bookwise.controller;

import org.example.bookwise.Exchange;
import org.example.bookwise.SessionStore;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
    private final ChatClient chatClient;
    private final SessionStore sessionStore;

    public HomeController(ChatClient.Builder chatClientBuilder, SessionStore sessionStore) {
        this.chatClient = chatClientBuilder.build();
        this.sessionStore = sessionStore;
    }

    @RequestMapping("/")
    public String welcome() {
        return "login";
    }

    @GetMapping("/home_page")
    public String homePage(Model model) {
        // Add user info and chat history to model
        if (sessionStore.getCurrentUser() != null) {
            model.addAttribute("username", sessionStore.getCurrentUser().getUsername());
        }
        model.addAttribute("history", sessionStore.getChatHistory());
        return "home_page";
    }

    @PostMapping("/home_page")
    public String chat(String message) {
        String aiResponse = simpleChat(message);
        sessionStore.addExchange(new Exchange(message, aiResponse));
        return "redirect:/home_page";
    }

    @PostMapping("/ask")
    public String ask(@RequestParam String prompt, Model model) {
        // Enhanced system prompt for better book recommendations
        String systemPrompt = """
            You are a knowledgeable book recommendation assistant. When recommending books:
            1. Always put the book title in quotes, like "Title" by Author Name
            2. Keep your response concise and friendly
            3. Recommend only ONE book per response
            4. Include a brief explanation of why you're recommending it
            5. Format: I recommend "Book Title" by Author Name. [Brief reason why]
            
            Example: I recommend "To Kill a Mockingbird" by Harper Lee. It's a powerful story about justice and morality in the American South.
            """;

        // Send user prompt to AI with system context
        String aiResponse = chatClient
                .prompt()
                .system(systemPrompt)
                .user(prompt)
                .call()
                .content();

        // Save to session history
        sessionStore.addExchange(new Exchange(prompt, aiResponse));

        // Add username and history to model
        if (sessionStore.getCurrentUser() != null) {
            model.addAttribute("username", sessionStore.getCurrentUser().getUsername());
        }
        model.addAttribute("history", sessionStore.getChatHistory());

        return "home_page";
    }

    private String simpleChat(String message) {
        return chatClient.prompt(message).call().content();
    }
}