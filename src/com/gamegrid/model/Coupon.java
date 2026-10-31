package com.gamegrid.model;

/**
 * Promotional discount code model.
 */
public class Coupon {
    private String code;
    private double discountPercentage;
    private double minOrderValue;
    private boolean active;

    public Coupon(String code, double discountPercentage, double minOrderValue, boolean active) {
        this.code = code != null ? code.trim().toUpperCase() : "";
        this.discountPercentage = Math.max(1.0, Math.min(80.0, discountPercentage));
        this.minOrderValue = Math.max(0.0, minOrderValue);
        this.active = active;
    }

    public String getCode() {
        return code;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public double getMinOrderValue() {
        return minOrderValue;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double calculateDiscount(double subtotal) {
        if (!active || subtotal < minOrderValue) {
            return 0.0;
        }
        double discount = subtotal * (discountPercentage / 100.0);
        return Math.round(discount * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return String.format("Coupon[%s]: %d%% OFF on orders over $%.2f (%s)",
                code, (int) discountPercentage, minOrderValue, active ? "Active" : "Inactive");
    }
}
