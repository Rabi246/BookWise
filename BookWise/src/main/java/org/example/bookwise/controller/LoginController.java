package org.example.bookwise.controller;

import jakarta.servlet.http.HttpSession;
import org.example.bookwise.model.User;
import org.example.bookwise.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login.jte";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {

        var user = userService.login(username, password);
        if (user != null) {
            session.setAttribute("currentUser", user);
            return "redirect:/library";
        }

        model.addAttribute("error", "Invalid username or password");
        return "login.jte";
    }

    @GetMapping("/library")
    public String libraryPage(HttpSession session, Model model) {

        var user = (User)session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        return "library.jte";
    }


}
