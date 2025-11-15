package org.example.bookwise.controller;

import jakarta.servlet.http.HttpSession;
import org.example.bookwise.UserService;
import org.example.bookwise.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    private final UserService userService;

    public HomeController(UserService userService) {
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
}