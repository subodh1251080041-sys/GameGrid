package com.gamegrid.exception;

public class GameAlreadyOwnedException extends GameGridException {
    public GameAlreadyOwnedException(String gameTitle) {
        super(String.format("You already own '%s' in your Game Grid library.", gameTitle));
    }
}
