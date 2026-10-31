package com.gamegrid.model;

/**
 * Early Access title with community roadmap and supporter incentive discounts.
 * Demonstrates Inheritance and Runtime Polymorphism.
 */
public class EarlyAccessGame extends Game {
    private String buildVersion;
    private double earlySupporterDiscount; // Additional discount percentage for early adopters

    public EarlyAccessGame(String id, String title, Genre genre, String publisher,
                            double basePrice, double discountPercentage, double rating,
                            int releaseYear, double sizeGb, String buildVersion,
                            double earlySupporterDiscount) {
        super(id, title, genre, publisher, basePrice, discountPercentage, rating, releaseYear, sizeGb);
        this.buildVersion = (buildVersion != null && !buildVersion.trim().isEmpty()) ? buildVersion.trim() : "v0.1-alpha";
        this.earlySupporterDiscount = Math.max(0.0, Math.min(30.0, earlySupporterDiscount));
    }

    @Override
    public double calculateFinalPrice() {
        // Combines store promotional discount with early supporter incentive
        double totalDiscountPercent = Math.min(90.0, getDiscountPercentage() + earlySupporterDiscount);
        double discounted = getBasePrice() * (1.0 - (totalDiscountPercent / 100.0));
        return Math.max(0.0, Math.round(discounted * 100.0) / 100.0);
    }

    @Override
    public String getDisplayType() {
        return "Early Access ⚡";
    }

    @Override
    public String getDetailsSummary() {
        return String.format("Early Build [%s] | Includes %d%% supporter discount",
                buildVersion, (int) earlySupporterDiscount);
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public void setBuildVersion(String buildVersion) {
        this.buildVersion = buildVersion;
    }

    public double getEarlySupporterDiscount() {
        return earlySupporterDiscount;
    }

    public void setEarlySupporterDiscount(double earlySupporterDiscount) {
        this.earlySupporterDiscount = earlySupporterDiscount;
    }
}
