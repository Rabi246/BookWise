package org.example.bookwise;

import java.util.Optional;
import org.example.bookwise.model.User;
import org.example.bookwise.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository users;

    public UserService(UserRepository users) {
        this.users = users;
    }

    public Optional<User> login(String username, String rawPassword) {
        return users.findByUsername(username)
                .filter(u -> BCrypt.checkpw(rawPassword, u.getPasswordHash()));
    }

    @Transactional
    public boolean register(String name, String username, String email, String rawPassword) {
        if (users.existsByUsername(username) || users.existsByEmail(email)) {
            return false;
        }
        String hash = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        users.save(new User(name, username, email, hash));
        return true;
    }

    public Optional<User> findByUsernameAndEmail(String username, String email) {
        return users.findByUsernameAndEmail(username, email);
    }

    @Transactional
    public void updatePassword(User user, String rawPassword) {
        String hash = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        user.setPasswordHash(hash);
        users.save(user);
    }

    public Optional<User> findById(Integer id) {
        return users.findById(id);
    }
}
