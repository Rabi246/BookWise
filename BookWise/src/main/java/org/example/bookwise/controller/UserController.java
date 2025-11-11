package org.example.bookwise.controller;


import org.example.bookwise.DataStore;
import org.example.bookwise.SessionStore;
import org.example.bookwise.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    private DataStore dataStore;
    private SessionStore sessionStore;

    public UserController(DataStore dataStore, SessionStore sessionStore) {
        this.dataStore = dataStore;
        this.sessionStore = sessionStore;
    }
    @GetMapping("/login")
    public String login()
    {
        return "login";
    }

    @PostMapping("/login")
    public String login(String username, String password) {
        User user = dataStore.login(username, password);
        if (user == null) {
            return "redirect:/login";
        }
        else
        {
            sessionStore.setCurrentUser(user);
            return "redirect:/home_page";
        }

    }

    @RequestMapping("/logout")
    public String logout() {
        sessionStore.setCurrentUser(null);
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("error", null);
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String name,
                               @RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String confirmPassword,
                               Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "register";
        }

        boolean created = dataStore.addUser(name, username, email, password);

        if (!created) {
            model.addAttribute("error", "Username or Email already exists!");
            return "register";
        }

        return "redirect:/login";
    }

    @GetMapping("/forgot_password")
    public String forgotPasswordForm(Model model) {
        model.addAttribute("error", null);
        return "forgot_password";
    }

    @PostMapping("/forgot_password")
    public String verifyUser(@RequestParam String username,
                             @RequestParam String email,
                             Model model) {

        User user = dataStore.findByUsernameAndEmail(username, email);

        if (user == null) {
            model.addAttribute("error", "No account found for that username & email.");
            return "forgot_password";
        }

        // temporarily store user for password reset
        sessionStore.setCurrentUser(user);

        return "reset_password";
    }

    @PostMapping("/reset_password")
    public String resetPassword(@RequestParam String password,
                                @RequestParam String confirmPassword,
                                Model model) {

        User user = sessionStore.getCurrentUser();

        if (user == null) {
            return "redirect:/forgot_password";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "reset_password";
        }

        dataStore.updatePassword(user, password);

        sessionStore.setCurrentUser(null);
        return "password_reset_success";
    }

}
