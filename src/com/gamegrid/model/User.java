package com.gamegrid.model;

import java.util.Objects;

/**
 * Abstract base class for all system users (Customers and Administrators).
 * Demonstrates Abstraction and Encapsulation.
 */
public abstract class User {
    private String id;
    private String username;
    private String email;
    private String password;

    public User(String id, String username, String email, String password) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        this.id = id.trim();
        this.username = username.trim();
        this.email = email != null ? email.trim() : "";
        this.password = password != null ? password : "";
    }

    public abstract String getRole();

    public abstract String getDashboardGreeting();

    // Encapsulation - Getters & Setters
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username != null && !username.trim().isEmpty()) {
            this.username = username.trim();
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim() : "";
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password != null ? password : "";
    }

    public boolean verifyPassword(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s)", getRole(), username, email);
    }
}
