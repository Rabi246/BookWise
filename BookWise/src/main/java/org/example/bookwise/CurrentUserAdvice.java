package org.example.bookwise;

import jakarta.servlet.http.HttpSession;
import org.example.bookwise.model.User;
import org.example.bookwise.UserService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CurrentUserAdvice {
    private final UserService userService;

    public CurrentUserAdvice(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("username")
    public String addUsername(HttpSession session) {
        Integer userId = (Integer) (session != null ? session.getAttribute("currentUserId") : null);
        if (userId == null) return null;
        return userService.findById(userId).map(User::getUsername).orElse(null);
    }
}
