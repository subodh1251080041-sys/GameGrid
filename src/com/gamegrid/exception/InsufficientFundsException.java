package com.gamegrid.exception;

public class InsufficientFundsException extends GameGridException {
    private double currentBalance;
    private double requiredAmount;

    public InsufficientFundsException(double currentBalance, double requiredAmount) {
        super(String.format("Insufficient wallet funds. Current balance: $%.2f, Required: $%.2f",
                currentBalance, requiredAmount));
        this.currentBalance = currentBalance;
        this.requiredAmount = requiredAmount;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public double getRequiredAmount() {
        return requiredAmount;
    }
}
