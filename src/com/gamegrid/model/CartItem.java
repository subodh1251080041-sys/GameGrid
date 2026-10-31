package com.gamegrid.model;

/**
 * Represents a single item added to the user's shopping cart.
 * Demonstrates Object Composition.
 */
public class CartItem {
    private Game game;
    private double priceAtAddition;

    public CartItem(Game game) {
        if (game == null) {
            throw new IllegalArgumentException("Cart item game cannot be null.");
        }
        this.game = game;
        this.priceAtAddition = game.calculateFinalPrice();
    }

    public Game getGame() {
        return game;
    }

    public double getPriceAtAddition() {
        return priceAtAddition;
    }

    public void refreshPrice() {
        this.priceAtAddition = game.calculateFinalPrice();
    }

    @Override
    public String toString() {
        return String.format("%s - $%.2f", game.getTitle(), priceAtAddition);
    }
}
