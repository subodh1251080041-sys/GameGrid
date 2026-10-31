package com.gamegrid.model;

/**
 * Standard edition of a digital game.
 * Demonstrates Inheritance and Runtime Polymorphism.
 */
public class StandardGame extends Game {
    private boolean includesDrmFreeCopy;

    public StandardGame(String id, String title, Genre genre, String publisher,
                        double basePrice, double discountPercentage, double rating,
                        int releaseYear, double sizeGb, boolean includesDrmFreeCopy) {
        super(id, title, genre, publisher, basePrice, discountPercentage, rating, releaseYear, sizeGb);
        this.includesDrmFreeCopy = includesDrmFreeCopy;
    }

    @Override
    public double calculateFinalPrice() {
        double discounted = getBasePrice() * (1.0 - (getDiscountPercentage() / 100.0));
        return Math.max(0.0, Math.round(discounted * 100.0) / 100.0);
    }

    @Override
    public String getDisplayType() {
        return "Standard Edition";
    }

    @Override
    public String getDetailsSummary() {
        return "Standard release" + (includesDrmFreeCopy ? " [DRM-Free Available]" : " [Cloud License]");
    }

    public boolean isIncludesDrmFreeCopy() {
        return includesDrmFreeCopy;
    }

    public void setIncludesDrmFreeCopy(boolean includesDrmFreeCopy) {
        this.includesDrmFreeCopy = includesDrmFreeCopy;
    }
}
