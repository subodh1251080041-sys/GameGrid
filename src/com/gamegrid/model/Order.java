package com.gamegrid.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a confirmed transaction receipt.
 */
public class Order {
    private String orderId;
    private String customerId;
    private String customerUsername;
    private List<String> gameTitles;
    private double subtotal;
    private double discountAmount;
    private double finalTotal;
    private String paymentMethod;
    private String timestamp;

    public Order(String orderId, String customerId, String customerUsername,
                 List<String> gameTitles, double subtotal, double discountAmount,
                 double finalTotal, String paymentMethod) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerUsername = customerUsername;
        this.gameTitles = new ArrayList<>(gameTitles);
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.finalTotal = finalTotal;
        this.paymentMethod = paymentMethod;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public Order(String orderId, String customerId, String customerUsername,
                 List<String> gameTitles, double subtotal, double discountAmount,
                 double finalTotal, String paymentMethod, String timestamp) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerUsername = customerUsername;
        this.gameTitles = new ArrayList<>(gameTitles);
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.finalTotal = finalTotal;
        this.paymentMethod = paymentMethod;
        this.timestamp = timestamp;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerUsername() {
        return customerUsername;
    }

    public List<String> getGameTitles() {
        return gameTitles;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getFinalTotal() {
        return finalTotal;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
