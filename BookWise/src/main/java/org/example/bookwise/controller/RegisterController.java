package org.example.bookwise.controller;

import org.example.bookwise.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register.jte";
    }

    @PostMapping("/register")
    public String processRegister(@RequestParam String username,
                                  @RequestParam String password,
                                  Model model) {

        boolean success = userService.register(username, password);

        if (!success) {
            model.addAttribute("error", "Username already exists!");
            return "register.jte";
        }

        return "redirect:/login";
    }

}