package com.gamegrid.service;

import com.gamegrid.exception.GameNotFoundException;
import com.gamegrid.model.Game;
import com.gamegrid.model.Genre;
import com.gamegrid.repository.GameRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service managing the game catalog.
 * Demonstrates:
 * 1. Compile-Time Polymorphism (Method Overloading for searching)
 * 2. Business Logic encapsulation
 * 3. Sorting & Filtering using Java Collections
 */
public class CatalogService {
    private final GameRepository gameRepository;

    public CatalogService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public Game getGameById(String id) throws GameNotFoundException {
        return gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException("No game found with ID: " + id));
    }

    // ==========================================
    // METHOD OVERLOADING (Compile-Time Polymorphism)
    // ==========================================

    /**
     * Search 1: By search query (matches title or publisher)
     */
    public List<Game> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllGames();
        }
        String q = query.trim().toLowerCase();
        return getAllGames().stream()
                .filter(g -> g.getTitle().toLowerCase().contains(q) || g.getPublisher().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    /**
     * Search 2: Overloaded - Filter by genre
     */
    public List<Game> search(Genre genre) {
        if (genre == null) {
            return getAllGames();
        }
        return getAllGames().stream()
                .filter(g -> g.getGenre() == genre)
                .collect(Collectors.toList());
    }

    /**
     * Search 3: Overloaded - Filter by price range
     */
    public List<Game> search(double minPrice, double maxPrice) {
        return getAllGames().stream()
                .filter(g -> {
                    double finalPrice = g.calculateFinalPrice();
                    return finalPrice >= minPrice && finalPrice <= maxPrice;
                })
                .collect(Collectors.toList());
    }

    /**
     * Search 4: Overloaded - Filter by genre and maximum budget
     */
    public List<Game> search(Genre genre, double maxPrice) {
        return getAllGames().stream()
                .filter(g -> (genre == null || g.getGenre() == genre) && g.calculateFinalPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    // ==========================================
    // SORTING CAPABILITIES
    // ==========================================
    public List<Game> getGamesSortedByRating() {
        List<Game> games = new ArrayList<>(getAllGames());
        Collections.sort(games); // Natural ordering from Comparable<Game>
        return games;
    }

    public List<Game> getGamesSortedByPrice(boolean ascending) {
        List<Game> games = new ArrayList<>(getAllGames());
        games.sort((g1, g2) -> {
            int comp = Double.compare(g1.calculateFinalPrice(), g2.calculateFinalPrice());
            return ascending ? comp : -comp;
        });
        return games;
    }

    public List<Game> getOnSaleGames() {
        return getAllGames().stream()
                .filter(Game::hasDiscount)
                .sorted((g1, g2) -> Double.compare(g2.getDiscountPercentage(), g1.getDiscountPercentage()))
                .collect(Collectors.toList());
    }

    // ==========================================
    // ADMIN CATALOG MANAGEMENT
    // ==========================================
    public void addGame(Game game) {
        gameRepository.save(game);
    }

    public void updateDiscount(String gameId, double discountPercentage) throws GameNotFoundException {
        Game game = getGameById(gameId);
        game.setDiscountPercentage(discountPercentage);
        gameRepository.save(game);
    }

    public boolean removeGame(String gameId) {
        return gameRepository.deleteById(gameId);
    }
}
