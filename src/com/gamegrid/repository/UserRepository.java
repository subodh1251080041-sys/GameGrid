package com.gamegrid.repository;

import com.gamegrid.model.Admin;
import com.gamegrid.model.Customer;
import com.gamegrid.model.User;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * CSV-backed repository for User accounts (Customers & Admins).
 * Demonstrates Polymorphic storage and File I/O.
 */
public class UserRepository implements Repository<User, String> {
    private final File file;
    private final Map<String, User> userMap = new LinkedHashMap<>();

    public UserRepository(String filePath) {
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
        userMap.clear();
        if (!file.exists() || file.length() == 0) {
            seedDefaultUsers();
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
                User user = parseCsvLine(line);
                if (user != null) {
                    userMap.put(user.getUsername().toLowerCase(), user);
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Error reading users CSV. Seeding defaults: " + e.getMessage());
            seedDefaultUsers();
            flush();
        }
    }

    @Override
    public synchronized void flush() {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.println("role,id,username,email,password,walletBalance,ownedGames,wishlistGames,department");
            for (User user : userMap.values()) {
                writer.println(toCsvLine(user));
            }
        } catch (IOException e) {
            System.err.println("Error saving users to file: " + e.getMessage());
        }
    }

    private String toCsvLine(User user) {
        if (user instanceof Customer) {
            Customer c = (Customer) user;
            String owned = String.join(";", c.getOwnedGameIds());
            String wishlist = String.join(";", c.getWishlistGameIds());
            return String.format("CUSTOMER,\"%s\",\"%s\",\"%s\",\"%s\",%.2f,\"%s\",\"%s\",\"\"",
                    escape(c.getId()),
                    escape(c.getUsername()),
                    escape(c.getEmail()),
                    escape(c.getPassword()),
                    c.getWalletBalance(),
                    escape(owned),
                    escape(wishlist));
        } else if (user instanceof Admin) {
            Admin a = (Admin) user;
            return String.format("ADMIN,\"%s\",\"%s\",\"%s\",\"%s\",0.0,\"\",\"\",\"%s\"",
                    escape(a.getId()),
                    escape(a.getUsername()),
                    escape(a.getEmail()),
                    escape(a.getPassword()),
                    escape(a.getDepartment()));
        }
        return "";
    }

    private User parseCsvLine(String line) {
        List<String> tokens = parseCsvRow(line);
        if (tokens.size() < 5) return null;

        try {
            String role = tokens.get(0).trim().toUpperCase();
            String id = tokens.get(1);
            String username = tokens.get(2);
            String email = tokens.get(3);
            String password = tokens.get(4);

            if ("ADMIN".equals(role)) {
                String dept = tokens.size() > 8 ? tokens.get(8) : "Operations";
                return new Admin(id, username, email, password, dept);
            } else {
                double balance = tokens.size() > 5 && !tokens.get(5).isEmpty() ? Double.parseDouble(tokens.get(5)) : 0.0;
                Customer customer = new Customer(id, username, email, password, balance);
                if (tokens.size() > 6 && !tokens.get(6).isEmpty()) {
                    String[] owned = tokens.get(6).split(";");
                    for (String gId : owned) {
                        if (!gId.trim().isEmpty()) customer.addOwnedGame(gId.trim().toUpperCase());
                    }
                }
                if (tokens.size() > 7 && !tokens.get(7).isEmpty()) {
                    String[] wish = tokens.get(7).split(";");
                    for (String wId : wish) {
                        if (!wId.trim().isEmpty()) customer.addToWishlist(wId.trim().toUpperCase());
                    }
                }
                return customer;
            }
        } catch (Exception e) {
            System.err.println("Skipping malformed user CSV line: " + line);
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

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\"", "");
    }

    private void seedDefaultUsers() {
        userMap.clear();

        // Administrator account
        Admin admin = new Admin("U001", "admin", "admin@gamegrid.com", "admin123", "Store Operations");
        userMap.put(admin.getUsername().toLowerCase(), admin);

        // Preloaded customer with some games & balance
        Customer customer1 = new Customer("U002", "gamer_john", "john@email.com", "pass123", 150.00);
        customer1.addOwnedGame("G101");
        customer1.addOwnedGame("G106");
        customer1.addToWishlist("G102");
        customer1.addToWishlist("G103");
        userMap.put(customer1.getUsername().toLowerCase(), customer1);

        // Another customer
        Customer customer2 = new Customer("U003", "cyber_sam", "sam@email.com", "pass123", 80.00);
        customer2.addOwnedGame("G103");
        customer2.addToWishlist("G104");
        userMap.put(customer2.getUsername().toLowerCase(), customer2);
    }

    @Override
    public synchronized void save(User entity) {
        if (entity != null) {
            userMap.put(entity.getUsername().toLowerCase(), entity);
            flush();
        }
    }

    @Override
    public synchronized Optional<User> findById(String id) {
        if (id == null) return Optional.empty();
        for (User u : userMap.values()) {
            if (u.getId().equalsIgnoreCase(id)) {
                return Optional.of(u);
            }
        }
        return Optional.empty();
    }

    public synchronized Optional<User> findByUsername(String username) {
        if (username == null) return Optional.empty();
        return Optional.ofNullable(userMap.get(username.trim().toLowerCase()));
    }

    @Override
    public synchronized List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public synchronized boolean deleteById(String id) {
        String foundKey = null;
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            if (entry.getValue().getId().equalsIgnoreCase(id)) {
                foundKey = entry.getKey();
                break;
            }
        }
        if (foundKey != null) {
            userMap.remove(foundKey);
            flush();
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean existsById(String id) {
        return findById(id).isPresent();
    }
}
