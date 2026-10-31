package com.gamegrid.model;

/**
 * Interface representing items that can have promotional or seasonal discounts applied.
 * Demonstrates Abstraction and interface segregation in OOP.
 */
public interface Discountable {
    double getDiscountPercentage();
    void setDiscountPercentage(double discountPercentage);
    boolean hasDiscount();
}
