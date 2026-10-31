package com.gamegrid.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages the collection of games a customer intends to purchase.
 * Demonstrates Collections Framework (List) and Encapsulation.
 */
public class Cart {
    private List<CartItem> items;

    public Cart() {
        this.items = new ArrayList<>();
    }

    public boolean addItem(Game game) {
        if (game == null) return false;
        if (containsGame(game.getId())) {
            return false; // Digital games are 1-license per account
        }
        items.add(new CartItem(game));
        return true;
    }

    public boolean removeItem(String gameId) {
        return items.removeIf(item -> item.getGame().getId().equalsIgnoreCase(gameId));
    }

    public boolean containsGame(String gameId) {
        for (CartItem item : items) {
            if (item.getGame().getId().equalsIgnoreCase(gameId)) {
                return true;
            }
        }
        return false;
    }

    public double calculateSubtotal() {
        double sum = 0.0;
        for (CartItem item : items) {
            sum += item.getGame().calculateFinalPrice();
        }
        return Math.round(sum * 100.0) / 100.0;
    }

    public int getItemCount() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        items.clear();
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}
