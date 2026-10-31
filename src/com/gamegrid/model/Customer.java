package com.gamegrid.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Customer user in Game Grid who browses, purchases, and plays games.
 * Demonstrates Inheritance, Encapsulation, and Collections Framework (Set).
 */
public class Customer extends User {
    private double walletBalance;
    private Set<String> ownedGameIds;
    private Set<String> wishlistGameIds;
    private Cart cart;

    public Customer(String id, String username, String email, String password, double initialBalance) {
        super(id, username, email, password);
        this.walletBalance = Math.max(0.0, initialBalance);
        this.ownedGameIds = new LinkedHashSet<>();
        this.wishlistGameIds = new LinkedHashSet<>();
        this.cart = new Cart();
    }

    @Override
    public String getRole() {
        return "CUSTOMER";
    }

    @Override
    public String getDashboardGreeting() {
        return String.format("Welcome back, Gamer %s! Wallet: $%.2f | Library: %d games",
                getUsername(), walletBalance, ownedGameIds.size());
    }

    public double getWalletBalance() {
        return walletBalance;
    }

    public void addFunds(double amount) {
        if (amount > 0) {
            this.walletBalance = Math.round((this.walletBalance + amount) * 100.0) / 100.0;
        }
    }

    public boolean deductFunds(double amount) {
        if (amount <= 0) return false;
        if (this.walletBalance >= amount) {
            this.walletBalance = Math.round((this.walletBalance - amount) * 100.0) / 100.0;
            return true;
        }
        return false;
    }

    public boolean ownsGame(String gameId) {
        if (gameId == null) return false;
        return ownedGameIds.contains(gameId);
    }

    public boolean addOwnedGame(String gameId) {
        if (gameId == null) return false;
        wishlistGameIds.remove(gameId); // Auto-remove from wishlist if purchased
        return ownedGameIds.add(gameId);
    }

    public boolean addToWishlist(String gameId) {
        if (gameId == null || ownsGame(gameId)) return false;
        return wishlistGameIds.add(gameId);
    }

    public boolean removeFromWishlist(String gameId) {
        return wishlistGameIds.remove(gameId);
    }

    public Set<String> getOwnedGameIds() {
        return Collections.unmodifiableSet(ownedGameIds);
    }

    public Set<String> getWishlistGameIds() {
        return Collections.unmodifiableSet(wishlistGameIds);
    }

    public Cart getCart() {
        return cart;
    }
}
