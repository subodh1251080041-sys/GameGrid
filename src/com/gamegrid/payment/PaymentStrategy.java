package com.gamegrid.payment;

import com.gamegrid.exception.GameGridException;
import com.gamegrid.model.Customer;

/**
 * Strategy pattern interface for flexible payment processing.
 * Demonstrates Abstraction and Strategy Design Pattern.
 */
public interface PaymentStrategy {
    String getMethodName();
    boolean processPayment(Customer customer, double amount) throws GameGridException;
}
