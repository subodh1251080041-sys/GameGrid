package com.gamegrid.exception;

/**
 * Base checked exception for the Game Grid application.
 * Demonstrates custom exception hierarchy and robust error handling.
 */
public class GameGridException extends Exception {
    public GameGridException(String message) {
        super(message);
    }

    public GameGridException(String message, Throwable cause) {
        super(message, cause);
    }
}
