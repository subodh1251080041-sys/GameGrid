package com.gamegrid.service;

import com.gamegrid.exception.GameAlreadyOwnedException;
import com.gamegrid.exception.GameGridException;
import com.gamegrid.exception.InvalidCouponException;
import com.gamegrid.model.*;
import com.gamegrid.payment.PaymentStrategy;
import com.gamegrid.repository.CouponRepository;
import com.gamegrid.repository.OrderRepository;
import com.gamegrid.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service orchestrating order checkouts, coupon application, and payment processing.
 * Demonstrates:
 * 1. Strategy Pattern integration (polymorphic PaymentStrategy)
 * 2. Exception handling (GameAlreadyOwnedException, InvalidCouponException, InsufficientFundsException)
 * 3. Business transaction management
 */
public class CheckoutService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CouponRepository couponRepository;

    public CheckoutService(UserRepository userRepository, OrderRepository orderRepository, CouponRepository couponRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.couponRepository = couponRepository;
    }

    public Order checkoutCart(Customer customer, PaymentStrategy paymentStrategy, String couponCode)
            throws GameGridException {
        Cart cart = customer.getCart();
        if (cart.isEmpty()) {
            throw new GameGridException("Your shopping cart is currently empty.");
        }

        // Verify customer does not already own any game in cart
        for (CartItem item : cart.getItems()) {
            if (customer.ownsGame(item.getGame().getId())) {
                throw new GameAlreadyOwnedException(item.getGame().getTitle());
            }
        }

        double subtotal = cart.calculateSubtotal();
        double discountAmount = 0.0;

        // Apply promotional coupon if provided
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            Optional<Coupon> optCoupon = couponRepository.findById(couponCode.trim());
            if (optCoupon.isEmpty() || !optCoupon.get().isActive()) {
                throw new InvalidCouponException("Promo code '" + couponCode + "' is invalid or expired.");
            }
            Coupon coupon = optCoupon.get();
            if (subtotal < coupon.getMinOrderValue()) {
                throw new InvalidCouponException(String.format("Promo code '%s' requires a minimum order of $%.2f. Current: $%.2f",
                        coupon.getCode(), coupon.getMinOrderValue(), subtotal));
            }
            discountAmount = coupon.calculateDiscount(subtotal);
        }

        double finalTotal = Math.max(0.0, Math.round((subtotal - discountAmount) * 100.0) / 100.0);

        // Process payment via Strategy Pattern (Polymorphism)
        paymentStrategy.processPayment(customer, finalTotal);

        // Finalize transaction: Add games to library
        List<String> gameTitles = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            customer.addOwnedGame(item.getGame().getId());
            gameTitles.add(item.getGame().getTitle());
        }

        // Clear cart
        cart.clear();

        // Create Order receipt
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Order order = new Order(orderId, customer.getId(), customer.getUsername(),
                gameTitles, subtotal, discountAmount, finalTotal, paymentStrategy.getMethodName());

        // Persist order and customer state
        orderRepository.save(order);
        userRepository.save(customer);

        return order;
    }

    public Order buySingleGame(Customer customer, Game game, PaymentStrategy paymentStrategy, String couponCode)
            throws GameGridException {
        if (customer.ownsGame(game.getId())) {
            throw new GameAlreadyOwnedException(game.getTitle());
        }

        double subtotal = game.calculateFinalPrice();
        double discountAmount = 0.0;

        if (couponCode != null && !couponCode.trim().isEmpty()) {
            Optional<Coupon> optCoupon = couponRepository.findById(couponCode.trim());
            if (optCoupon.isEmpty() || !optCoupon.get().isActive()) {
                throw new InvalidCouponException("Promo code '" + couponCode + "' is invalid or expired.");
            }
            Coupon coupon = optCoupon.get();
            if (subtotal < coupon.getMinOrderValue()) {
                throw new InvalidCouponException(String.format("Promo code '%s' requires minimum order of $%.2f.",
                        coupon.getCode(), coupon.getMinOrderValue()));
            }
            discountAmount = coupon.calculateDiscount(subtotal);
        }

        double finalTotal = Math.max(0.0, Math.round((subtotal - discountAmount) * 100.0) / 100.0);

        paymentStrategy.processPayment(customer, finalTotal);

        customer.addOwnedGame(game.getId());

        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Order order = new Order(orderId, customer.getId(), customer.getUsername(),
                List.of(game.getTitle()), subtotal, discountAmount, finalTotal, paymentStrategy.getMethodName());

        orderRepository.save(order);
        userRepository.save(customer);

        return order;
    }
}
