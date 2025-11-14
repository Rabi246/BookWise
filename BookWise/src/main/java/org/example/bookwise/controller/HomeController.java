package org.example.bookwise.controller;

import jakarta.servlet.http.HttpSession;
import org.example.bookwise.UserService;
import org.example.bookwise.Exchange;
import org.example.bookwise.SessionStore;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import org.example.bookwise.model.User;


@Controller
public class HomeController {
    private final ChatClient chatClient;
    private final SessionStore sessionStore;
    private final UserService userService;

    public HomeController(ChatClient.Builder chatClientBuilder, SessionStore sessionStore, UserService userService) {
        this.chatClient = chatClientBuilder.build();
        this.sessionStore = sessionStore;
        this.userService = userService;
    }

    @RequestMapping("/")
    public String welcome() {
        return "login";
    }

    @GetMapping("/home_page")
    public String homePage(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("currentUserId");
        if (userId == null) {
            return "redirect:/login";
        }

        String username = userService.findById(userId)
                .map(User::getUsername)
                .orElse("Guest");

        model.addAttribute("username", username);
        return "home_page";
    }


    @PostMapping("/home_page")
    public String chat(String message) {
        String aiResponse = chatWithHistory(message);
        sessionStore.addExchange(new Exchange(message, aiResponse));
        return "redirect:/home_page";
    }

    @PostMapping("/ask")
    public String ask(@RequestParam String prompt, Model model) {
        // Get AI response with full conversation context
        String aiResponse = chatWithHistory(prompt);

        // Save to session history
        sessionStore.addExchange(new Exchange(prompt, aiResponse));

        // Add username and history to model
        if (sessionStore.getCurrentUser() != null) {
            model.addAttribute("username", sessionStore.getCurrentUser().getUsername());
        }
        model.addAttribute("history", sessionStore.getChatHistory());

        return "home_page";
    }

    /**
     * Chat with full conversation history for context-aware responses
     */
    private String chatWithHistory(String userMessage) {
        // System prompt for book recommendations
        String systemPrompt = """
            You are a knowledgeable and engaging book recommendation assistant. 
            
            Guidelines:
            1. ALWAYS format book titles as: "Title" by Author Name
            2. Recommend only ONE book per response
            3. Be conversational and remember what you've already recommended
            4. If asked for something different, recommend a DIFFERENT book
            5. Keep responses concise (2-3 sentences max)
            6. Show personality and enthusiasm about books
            
            Examples:
            - "I recommend "To Kill a Mockingbird" by Harper Lee. It's a powerful story about justice and morality."
            - "Try "The Name of the Wind" by Patrick Rothfuss. It's an immersive fantasy with beautiful prose."
            - "Check out "Project Hail Mary" by Andy Weir. A thrilling sci-fi adventure with humor and heart."
            """;

        // Build conversation history
        List<Message> messages = new ArrayList<>();

        // Add all previous exchanges for context
        for (Exchange exchange : sessionStore.getChatHistory()) {
            messages.add(new UserMessage(exchange.getUserMessage()));
            messages.add(new AssistantMessage(exchange.getAiMessage()));
        }

        // Add current user message
        messages.add(new UserMessage(userMessage));

        // Create prompt with system context and full history
        Prompt prompt = new Prompt(messages);

        // Get AI response
        return chatClient.prompt(prompt)
                .system(systemPrompt)
                .call()
                .content();
    }
}