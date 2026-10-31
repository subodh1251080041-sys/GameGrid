package com.gamegrid.service;

import com.gamegrid.exception.GameGridException;
import com.gamegrid.exception.GameNotFoundException;
import com.gamegrid.model.Customer;
import com.gamegrid.model.Game;
import com.gamegrid.model.Review;
import com.gamegrid.repository.GameRepository;
import com.gamegrid.repository.ReviewRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service managing customer game reviews and average rating computations.
 */
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final GameRepository gameRepository;

    public ReviewService(ReviewRepository reviewRepository, GameRepository gameRepository) {
        this.reviewRepository = reviewRepository;
        this.gameRepository = gameRepository;
    }

    public Review addReview(Customer customer, String gameId, int rating, String comment)
            throws GameGridException {
        if (!customer.ownsGame(gameId)) {
            throw new GameGridException("Verified purchase required: You can only review games you own.");
        }

        Optional<Game> optGame = gameRepository.findById(gameId);
        if (optGame.isEmpty()) {
            throw new GameNotFoundException("Game not found for ID: " + gameId);
        }

        String reviewId = "REV-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Review review = new Review(reviewId, gameId.toUpperCase(), customer.getUsername(), rating, comment);
        reviewRepository.save(review);

        // Update game's aggregated rating
        recalculateGameRating(gameId);

        return review;
    }

    public List<Review> getReviewsForGame(String gameId) {
        return reviewRepository.findByGameId(gameId);
    }

    private void recalculateGameRating(String gameId) {
        List<Review> gameReviews = reviewRepository.findByGameId(gameId);
        if (gameReviews.isEmpty()) return;

        double sum = 0.0;
        for (Review r : gameReviews) {
            sum += r.getRating();
        }
        double avg = sum / gameReviews.size();

        Optional<Game> optGame = gameRepository.findById(gameId);
        if (optGame.isPresent()) {
            Game game = optGame.get();
            game.setRating(avg);
            gameRepository.save(game);
        }
    }
}
