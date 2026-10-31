package com.gamegrid.repository;

import com.gamegrid.model.Order;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * CSV-backed repository for customer order history and store transaction logs.
 */
public class OrderRepository implements Repository<Order, String> {
    private final File file;
    private final List<Order> orders = new ArrayList<>();

    public OrderRepository(String filePath) {
        this.file = new File(filePath);
        ensureDataDirectoryExists();
        reload();
    }

    private void ensureDataDirectoryExists() {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    @Override
    public synchronized void reload() {
        orders.clear();
        if (!file.exists() || file.length() == 0) {
            seedDefaultOrders();
            flush();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                Order order = parseCsvLine(line);
                if (order != null) {
                    orders.add(order);
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Error reading orders CSV: " + e.getMessage());
        }
    }

    @Override
    public synchronized void flush() {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.println("orderId,customerId,customerUsername,gameTitles,subtotal,discountAmount,finalTotal,paymentMethod,timestamp");
            for (Order o : orders) {
                writer.println(String.format("\"%s\",\"%s\",\"%s\",\"%s\",%.2f,%.2f,%.2f,\"%s\",\"%s\"",
                        o.getOrderId(),
                        o.getCustomerId(),
                        o.getCustomerUsername(),
                        String.join(";", o.getGameTitles()),
                        o.getSubtotal(),
                        o.getDiscountAmount(),
                        o.getFinalTotal(),
                        o.getPaymentMethod(),
                        o.getTimestamp()));
            }
        } catch (IOException e) {
            System.err.println("Error saving orders to file: " + e.getMessage());
        }
    }

    private Order parseCsvLine(String line) {
        List<String> tokens = parseCsvRow(line);
        if (tokens.size() < 9) return null;
        try {
            String orderId = tokens.get(0);
            String customerId = tokens.get(1);
            String username = tokens.get(2);
            String titlesRaw = tokens.get(3);
            List<String> titles = Arrays.asList(titlesRaw.split(";"));
            double subtotal = Double.parseDouble(tokens.get(4));
            double discount = Double.parseDouble(tokens.get(5));
            double total = Double.parseDouble(tokens.get(6));
            String method = tokens.get(7);
            String timestamp = tokens.get(8);

            return new Order(orderId, customerId, username, titles, subtotal, discount, total, method, timestamp);
        } catch (Exception e) {
            System.err.println("Skipping malformed order CSV row: " + line);
            return null;
        }
    }

    private List<String> parseCsvRow(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString().trim());
        return tokens;
    }

    private void seedDefaultOrders() {
        orders.clear();
        orders.add(new Order("ORD-9001", "U002", "gamer_john",
                List.of("The Witcher 3: Wild Hunt", "Hollow Knight"),
                27.49, 0.0, 27.49, "Game Grid Digital Wallet", "2026-10-25 14:32:10"));
        orders.add(new Order("ORD-9002", "U003", "cyber_sam",
                List.of("Elden Ring"),
                50.99, 10.0, 40.99, "Credit/Debit Card (•••• 4242)", "2026-10-28 18:20:05"));
    }

    @Override
    public synchronized void save(Order entity) {
        if (entity != null) {
            orders.add(entity);
            flush();
        }
    }

    @Override
    public synchronized Optional<Order> findById(String id) {
        if (id == null) return Optional.empty();
        return orders.stream()
                .filter(o -> o.getOrderId().equalsIgnoreCase(id))
                .findFirst();
    }

    public synchronized List<Order> findByCustomerId(String customerId) {
        if (customerId == null) return Collections.emptyList();
        List<Order> res = new ArrayList<>();
        for (Order o : orders) {
            if (o.getCustomerId().equalsIgnoreCase(customerId)) {
                res.add(o);
            }
        }
        return res;
    }

    @Override
    public synchronized List<Order> findAll() {
        return new ArrayList<>(orders);
    }

    @Override
    public synchronized boolean deleteById(String id) {
        boolean removed = orders.removeIf(o -> o.getOrderId().equalsIgnoreCase(id));
        if (removed) flush();
        return removed;
    }

    @Override
    public synchronized boolean existsById(String id) {
        return findById(id).isPresent();
    }
}
