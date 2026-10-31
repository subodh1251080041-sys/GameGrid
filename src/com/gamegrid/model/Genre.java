package com.gamegrid.model;

public enum Genre {
    ACTION,
    RPG,
    ADVENTURE,
    STRATEGY,
    SHOOTER,
    INDIE,
    SIMULATION,
    SPORTS,
    HORROR;

    public static Genre fromString(String text) {
        if (text == null) return ACTION;
        for (Genre g : Genre.values()) {
            if (g.name().equalsIgnoreCase(text.trim())) {
                return g;
            }
        }
        return ACTION;
    }
}
