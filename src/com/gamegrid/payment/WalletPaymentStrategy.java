package com.gamegrid.payment;

import com.gamegrid.exception.InsufficientFundsException;
import com.gamegrid.model.Customer;

/**
 * Concrete payment strategy using the customer's in-store Game Grid Wallet balance.
 */
public class WalletPaymentStrategy implements PaymentStrategy {

    @Override
    public String getMethodName() {
        return "Game Grid Digital Wallet";
    }

    @Override
    public boolean processPayment(Customer customer, double amount) throws InsufficientFundsException {
        if (customer.getWalletBalance() < amount) {
            throw new InsufficientFundsException(customer.getWalletBalance(), amount);
        }
        return customer.deductFunds(amount);
    }
}
