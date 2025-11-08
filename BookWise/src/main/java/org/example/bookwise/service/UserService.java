package org.example.bookwise.service;

import org.example.bookwise.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private List<User> users = new ArrayList<>();

    public boolean register(String username, String password) {
        if (getUser(username) != null) return false;

        users.add(new User(username, password));
        return true;
    }

    public User login(String username, String password) {
        User user = getUser(username);
        if (user != null && user.getPassword().equals(password))
            return user;

        return null;
    }

    public User getUser(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }
}
