package com.gamegrid.payment;

import com.gamegrid.exception.GameGridException;
import com.gamegrid.model.Customer;

/**
 * Concrete payment strategy simulating Credit/Debit card authorization.
 */
public class CreditCardPaymentStrategy implements PaymentStrategy {
    private String cardNumber;
    private String cardHolder;
    private String expiry;

    public CreditCardPaymentStrategy(String cardNumber, String cardHolder, String expiry) {
        this.cardNumber = cardNumber != null ? cardNumber.replaceAll("\\s+", "") : "";
        this.cardHolder = cardHolder != null ? cardHolder.trim() : "Valued Customer";
        this.expiry = expiry != null ? expiry.trim() : "12/28";
    }

    @Override
    public String getMethodName() {
        String masked = cardNumber.length() >= 4 
                ? "•••• •••• •••• " + cardNumber.substring(cardNumber.length() - 4)
                : "•••• 4242";
        return "Credit/Debit Card (" + masked + ")";
    }

    @Override
    public boolean processPayment(Customer customer, double amount) throws GameGridException {
        if (cardNumber.length() < 12) {
            throw new GameGridException("Invalid card number format. Must be at least 12-16 digits.");
        }
        // Simulated gateway communication and validation
        return true;
    }
}
