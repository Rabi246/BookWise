package org.example.bookwise;

import org.example.bookwise.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataStore {
    private List<User> users;

    public DataStore() {
        users = new ArrayList<>();
        users.add(new User(1, "John Doe", "user1", "user1@email.com", "password1"));
        users.add(new User(2, "Jane Smith", "user2", "user2@email.com", "password2"));
    }
    public User login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
    public boolean addUser(String name, String username, String email, String password) {

        // check if username OR email already exists
        for (User u : users) {
            if (u.getUsername().equals(username) || u.getEmail().equals(email)) {
                return false; // username/email taken
            }
        }

        int newId = users.size() + 1;
        users.add(new User(newId, name, username, email, password));
        return true;
    }
    public User findByUsernameAndEmail(String username, String email) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getEmail().equals(email)) {
                return u;
            }
        }
        return null;
    }
    public void updatePassword(User user, String newPassword) {
        user.setPassword(newPassword);
    }


}
