package com.gamegrid.model;

/**
 * Interface representing any item in Game Grid that can be purchased.
 * Demonstrates Abstraction in OOP.
 */
public interface Purchasable {
    String getId();
    String getTitle();
    double getBasePrice();
    double calculateFinalPrice();
    String getDisplayType();
}
