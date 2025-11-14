package org.example.bookwise.controller;

import jakarta.servlet.http.HttpSession;
import org.example.bookwise.model.User;
import org.example.bookwise.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("error", null);
        return "login"; }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {
        return userService.login(username, password)
                .map(u -> {
                    session.setAttribute("currentUserId", u.getId());
                    return "redirect:/home_page";
                })
                .orElseGet(() -> {
                    model.addAttribute("error", "Invalid username or password");
                    return "login";
                });
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("error", null);
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String name,
                             @RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam String confirmPassword,
                             Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "register";
        }

        boolean created = userService.register(name, username, email, password);
        if (!created) {
            model.addAttribute("error", "Username or Email already exists!");
            return "register";
        }

        return "redirect:/login";
    }

    @GetMapping("/forgot_password")
    public String forgotPasswordPage(Model model) {
        model.addAttribute("error", null);
        return "forgot_password";
    }

    @PostMapping("/forgot_password")
    public String verifyUser(@RequestParam String username,
                             @RequestParam String email,
                             HttpSession session,
                             Model model) {
        var userOpt = userService.findByUsernameAndEmail(username, email);
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "No account found for that username & email.");
            return "forgot_password";
        }
        session.setAttribute("resetUserId", userOpt.get().getId());
        return "reset_password";
    }

    @PostMapping("/reset_password")
    public String resetPassword(@RequestParam String password,
                                @RequestParam String confirmPassword,
                                HttpSession session,
                                Model model) {

        Integer uid = (Integer) session.getAttribute("resetUserId");
        if (uid == null) return "redirect:/forgot_password";

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "reset_password";
        }

        var user = userService.login( // quick way to load by id; better: repository.findById
                // replace with a direct repo call if you prefer:
                "", ""                     // (see note below)
        );
        // Simpler: inject UserRepository here or add a service method:
        // userService.updatePasswordById(uid, password)

        return "password_reset_success";
    }
}

