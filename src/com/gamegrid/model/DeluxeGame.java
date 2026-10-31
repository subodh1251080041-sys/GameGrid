package com.gamegrid.model;

/**
 * Deluxe edition of a game bundled with Season Pass, OST, and digital goods.
 * Demonstrates Inheritance and Runtime Polymorphism (overriding price calculation).
 */
public class DeluxeGame extends Game {
    private boolean seasonPassIncluded;
    private boolean soundtrackIncluded;
    private double bundlePremium; // Extra content value added to base price

    public DeluxeGame(String id, String title, Genre genre, String publisher,
                      double basePrice, double discountPercentage, double rating,
                      int releaseYear, double sizeGb, boolean seasonPassIncluded,
                      boolean soundtrackIncluded, double bundlePremium) {
        super(id, title, genre, publisher, basePrice, discountPercentage, rating, releaseYear, sizeGb);
        this.seasonPassIncluded = seasonPassIncluded;
        this.soundtrackIncluded = soundtrackIncluded;
        this.bundlePremium = Math.max(0.0, bundlePremium);
    }

    @Override
    public double calculateFinalPrice() {
        // Base price + premium for bonus DLC/OST, with store discount applied
        double totalBase = getBasePrice() + bundlePremium;
        double discounted = totalBase * (1.0 - (getDiscountPercentage() / 100.0));
        return Math.max(0.0, Math.round(discounted * 100.0) / 100.0);
    }

    @Override
    public String getDisplayType() {
        return "Deluxe Edition ★";
    }

    @Override
    public String getDetailsSummary() {
        StringBuilder sb = new StringBuilder("Includes: Base Game");
        if (seasonPassIncluded) sb.append(" + Season Pass");
        if (soundtrackIncluded) sb.append(" + Digital OST & Artbook");
        sb.append(String.format(" (+$%.2f premium value)", bundlePremium));
        return sb.toString();
    }

    public boolean isSeasonPassIncluded() {
        return seasonPassIncluded;
    }

    public void setSeasonPassIncluded(boolean seasonPassIncluded) {
        this.seasonPassIncluded = seasonPassIncluded;
    }

    public boolean isSoundtrackIncluded() {
        return soundtrackIncluded;
    }

    public void setSoundtrackIncluded(boolean soundtrackIncluded) {
        this.soundtrackIncluded = soundtrackIncluded;
    }

    public double getBundlePremium() {
        return bundlePremium;
    }

    public void setBundlePremium(double bundlePremium) {
        this.bundlePremium = Math.max(0.0, bundlePremium);
    }
}
