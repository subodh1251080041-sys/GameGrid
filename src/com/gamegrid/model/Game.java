package com.gamegrid.model;

import java.util.Objects;

/**
 * Abstract base class for all games in Game Grid.
 * Demonstrates:
 * 1. Abstraction (cannot be instantiated directly)
 * 2. Encapsulation (private fields with validated accessors)
 * 3. Polymorphic contract via abstract calculateFinalPrice()
 */
public abstract class Game implements Purchasable, Discountable, Comparable<Game> {
    private String id;
    private String title;
    private Genre genre;
    private String publisher;
    private double basePrice;
    private double discountPercentage; // e.g. 20.0 for 20% off
    private double rating;             // 1.0 - 5.0
    private int releaseYear;
    private double sizeGb;             // Download size in GB

    // Parameterized constructor
    public Game(String id, String title, Genre genre, String publisher,
                double basePrice, double discountPercentage, double rating,
                int releaseYear, double sizeGb) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Game ID cannot be empty.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Game title cannot be empty.");
        }
        if (basePrice < 0) {
            throw new IllegalArgumentException("Base price cannot be negative.");
        }
        this.id = id.trim();
        this.title = title.trim();
        this.genre = genre != null ? genre : Genre.ACTION;
        this.publisher = publisher != null ? publisher.trim() : "Independent";
        this.basePrice = basePrice;
        setDiscountPercentage(discountPercentage);
        setRating(rating);
        this.releaseYear = releaseYear;
        this.sizeGb = Math.max(0.1, sizeGb);
    }

    // Abstract methods to be implemented by specialized game types (Polymorphism)
    @Override
    public abstract double calculateFinalPrice();

    @Override
    public abstract String getDisplayType();

    public abstract String getDetailsSummary();

    // Concrete implementation of Discountable
    @Override
    public double getDiscountPercentage() {
        return discountPercentage;
    }

    @Override
    public void setDiscountPercentage(double discountPercentage) {
        if (discountPercentage < 0.0) {
            this.discountPercentage = 0.0;
        } else if (discountPercentage > 90.0) {
            this.discountPercentage = 90.0; // Max allowable discount is 90%
        } else {
            this.discountPercentage = discountPercentage;
        }
    }

    @Override
    public boolean hasDiscount() {
        return discountPercentage > 0.001;
    }

    // Encapsulation - Getters and Setters
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title.trim();
        }
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        if (genre != null) {
            this.genre = genre;
        }
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Override
    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        if (basePrice >= 0) {
            this.basePrice = basePrice;
        }
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        if (rating < 0.0) {
            this.rating = 0.0;
        } else if (rating > 5.0) {
            this.rating = 5.0;
        } else {
            this.rating = Math.round(rating * 10.0) / 10.0;
        }
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public double getSizeGb() {
        return sizeGb;
    }

    public void setSizeGb(double sizeGb) {
        this.sizeGb = Math.max(0.1, sizeGb);
    }

    @Override
    public int compareTo(Game other) {
        // Natural ordering: highest rating first, then alphabetical by title
        int ratingComp = Double.compare(other.rating, this.rating);
        if (ratingComp != 0) return ratingComp;
        return this.title.compareToIgnoreCase(other.title);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(id, game.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | $%.2f", id, title, genre, calculateFinalPrice());
    }
}
