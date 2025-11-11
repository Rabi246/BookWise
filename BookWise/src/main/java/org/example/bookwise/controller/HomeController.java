package org.example.bookwise.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@Controller
public class HomeController {
    private final ChatClient chatClient;
    public HomeController(ChatClient.Builder chatClientBuilder)
    {
        this.chatClient = chatClientBuilder.build();
    }



    @RequestMapping("/")
    public String Welcome() {
        return "welcome";
    }

    @GetMapping("/home_page")
    public String home(Model model) {
        model.addAttribute("joke", "");
        return "home_page";
    }
    @PostMapping("/home_page")
    public String post_home(@RequestParam("category") String category, Model model) {

        String prompt = "Tell me a joke about " + category + ".";
        String joke = simpleChat(prompt);
        model.addAttribute("joke", joke);
        return "joke";
    }

    private String simpleChat(String message) {
        return chatClient.prompt(message).call().content();
    }
}

