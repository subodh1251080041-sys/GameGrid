package com.gamegrid.model;

/**
 * Factory pattern class to instantiate appropriate Game subclasses based on edition type.
 * Demonstrates Factory Design Pattern in OOP.
 */
public class GameFactory {

    public static Game createGame(String type, String id, String title, Genre genre,
                                  String publisher, double basePrice, double discountPercentage,
                                  double rating, int releaseYear, double sizeGb,
                                  String extraParam1, String extraParam2, String extraParam3) {
        if (type == null) type = "STANDARD";
        String normalizedType = type.trim().toUpperCase();

        switch (normalizedType) {
            case "DELUXE":
                boolean seasonPass = Boolean.parseBoolean(extraParam1);
                boolean soundtrack = Boolean.parseBoolean(extraParam2);
                double premium = (extraParam3 != null && !extraParam3.isEmpty()) 
                        ? Double.parseDouble(extraParam3) : 15.0;
                return new DeluxeGame(id, title, genre, publisher, basePrice,
                        discountPercentage, rating, releaseYear, sizeGb, seasonPass, soundtrack, premium);

            case "EARLY_ACCESS":
            case "EARLYACCESS":
                String buildVer = (extraParam1 != null && !extraParam1.isEmpty()) ? extraParam1 : "v0.8.0";
                double earlyDiscount = (extraParam2 != null && !extraParam2.isEmpty()) 
                        ? Double.parseDouble(extraParam2) : 10.0;
                return new EarlyAccessGame(id, title, genre, publisher, basePrice,
                        discountPercentage, rating, releaseYear, sizeGb, buildVer, earlyDiscount);

            case "STANDARD":
            default:
                boolean drmFree = Boolean.parseBoolean(extraParam1);
                return new StandardGame(id, title, genre, publisher, basePrice,
                        discountPercentage, rating, releaseYear, sizeGb, drmFree);
        }
    }
}
