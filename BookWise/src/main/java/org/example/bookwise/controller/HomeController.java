package org.example.bookwise.controller;

import org.example.bookwise.Exchange;
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

@Controller
public class HomeController {
    private final ChatClient chatClient;
    private final List<Exchange> history = new ArrayList<>();
    public HomeController(ChatClient.Builder chatClientBuilder)
    {
        this.chatClient = chatClientBuilder.build();
    }



    @RequestMapping("/")
    public String Welcome() {
        return "login";
    }

    @GetMapping("/home_page")
    public String chat(Model model) {
        model.addAttribute("history", history);
        return "home_page";
    }
    @PostMapping("/home_page")
    public String chat(String message) {
        history.add(new Exchange(message, simpleChat(message)));
        return "redirect:home_page";
    }

    private String simpleChat(String message) {
        return chatClient.prompt(message).call().content();
    }

    private String realChat(String message) {
        List<Message> messages = new ArrayList<>();
        for (var exchange : history) {
            messages.add(new UserMessage(exchange.getUserMessage()));
            messages.add(new AssistantMessage(exchange.getAiMessage()));
        }
        messages.add(new UserMessage(message));
        return chatClient.prompt(new Prompt(messages)).call().content();
    }
}

