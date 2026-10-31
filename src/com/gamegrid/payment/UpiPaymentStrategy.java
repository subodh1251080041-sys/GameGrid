package com.gamegrid.payment;

import com.gamegrid.exception.GameGridException;
import com.gamegrid.model.Customer;

/**
 * Concrete payment strategy simulating instant UPI (Unified Payments Interface) payments.
 */
public class UpiPaymentStrategy implements PaymentStrategy {
    private String upiId;

    public UpiPaymentStrategy(String upiId) {
        this.upiId = upiId != null ? upiId.trim() : "";
    }

    @Override
    public String getMethodName() {
        return "UPI Transfer (" + upiId + ")";
    }

    @Override
    public boolean processPayment(Customer customer, double amount) throws GameGridException {
        if (!upiId.contains("@") || upiId.length() < 5) {
            throw new GameGridException("Invalid UPI ID. Format should be username@bank (e.g., gamer@okaxis).");
        }
        // Simulated instant payment authorization
        return true;
    }
}
