package com.gamegrid.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Customer review and rating for a game.
 */
public class Review {
    private String id;
    private String gameId;
    private String username;
    private int rating; // 1 to 5
    private String comment;
    private String timestamp;

    public Review(String id, String gameId, String username, int rating, String comment) {
        this.id = id;
        this.gameId = gameId;
        this.username = username;
        this.rating = Math.max(1, Math.min(5, rating));
        this.comment = comment != null ? comment.trim() : "";
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public Review(String id, String gameId, String username, int rating, String comment, String timestamp) {
        this.id = id;
        this.gameId = gameId;
        this.username = username;
        this.rating = Math.max(1, Math.min(5, rating));
        this.comment = comment;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getGameId() {
        return gameId;
    }

    public String getUsername() {
        return username;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s★] %s by %s (%s)", rating, comment, username, timestamp);
    }
}
