package com.gamegrid.service;

import com.gamegrid.exception.AuthenticationException;
import com.gamegrid.model.Customer;
import com.gamegrid.model.User;
import com.gamegrid.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Service managing user authentication, sessions, and account registration.
 * Demonstrates Service Layer separation and Exception Handling.
 */
public class AuthService {
    private final UserRepository userRepository;
    private User currentUser;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String username, String password) throws AuthenticationException {
        if (username == null || username.trim().isEmpty() || password == null) {
            throw new AuthenticationException("Username and password cannot be empty.");
        }

        Optional<User> optUser = userRepository.findByUsername(username.trim());
        if (optUser.isEmpty()) {
            throw new AuthenticationException("Account not found for username: " + username);
        }

        User user = optUser.get();
        if (!user.verifyPassword(password)) {
            throw new AuthenticationException("Invalid password. Please try again.");
        }

        this.currentUser = user;
        return user;
    }

    public Customer registerCustomer(String username, String email, String password, double initialDeposit)
            throws AuthenticationException {
        if (username == null || username.trim().length() < 3) {
            throw new AuthenticationException("Username must be at least 3 characters.");
        }
        if (password == null || password.length() < 4) {
            throw new AuthenticationException("Password must be at least 4 characters.");
        }
        if (userRepository.findByUsername(username.trim()).isPresent()) {
            throw new AuthenticationException("Username '" + username + "' is already taken. Please choose another.");
        }

        String userId = "U" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Customer newCustomer = new Customer(userId, username.trim(), email != null ? email.trim() : "", password, initialDeposit);
        userRepository.save(newCustomer);
        this.currentUser = newCustomer;
        return newCustomer;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
