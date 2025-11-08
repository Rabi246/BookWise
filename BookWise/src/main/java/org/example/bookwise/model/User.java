package org.example.bookwise.model;

public class User {
    private String username;
    private String password;
    private String email; // optional for later

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Add second constructor if you want email
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }
}
