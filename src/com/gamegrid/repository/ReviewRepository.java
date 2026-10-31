package com.gamegrid.repository;

import com.gamegrid.model.Review;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * CSV-backed repository for customer ratings and reviews.
 */
public class ReviewRepository implements Repository<Review, String> {
    private final File file;
    private final List<Review> reviews = new ArrayList<>();

    public ReviewRepository(String filePath) {
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
        reviews.clear();
        if (!file.exists() || file.length() == 0) {
            seedDefaultReviews();
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
                Review r = parseCsvLine(line);
                if (r != null) {
                    reviews.add(r);
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Error reading reviews CSV: " + e.getMessage());
        }
    }

    @Override
    public synchronized void flush() {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.println("id,gameId,username,rating,comment,timestamp");
            for (Review r : reviews) {
                writer.println(String.format("\"%s\",\"%s\",\"%s\",%d,\"%s\",\"%s\"",
                        r.getId(),
                        r.getGameId(),
                        r.getUsername(),
                        r.getRating(),
                        escape(r.getComment()),
                        r.getTimestamp()));
            }
        } catch (IOException e) {
            System.err.println("Error saving reviews to file: " + e.getMessage());
        }
    }

    private Review parseCsvLine(String line) {
        List<String> tokens = parseCsvRow(line);
        if (tokens.size() < 6) return null;
        try {
            return new Review(tokens.get(0), tokens.get(1), tokens.get(2),
                    Integer.parseInt(tokens.get(3)), tokens.get(4), tokens.get(5));
        } catch (Exception e) {
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

    private void seedDefaultReviews() {
        reviews.clear();
        reviews.add(new Review("REV-1", "G101", "gamer_john", 5, "Masterpiece storytelling and atmosphere! 10/10.", "2026-10-26 11:20"));
        reviews.add(new Review("REV-2", "G103", "cyber_sam", 5, "Incredible open world and boss mechanics. Tough but rewarding.", "2026-10-29 19:15"));
        reviews.add(new Review("REV-3", "G104", "indie_fan", 5, "Even better combat flow than the first Hades. Phenomenal soundtrack!", "2026-10-30 14:05"));
        reviews.add(new Review("REV-4", "G106", "gamer_john", 5, "Best metroidvania ever created. Silksong when?!", "2026-10-27 22:30"));
    }

    @Override
    public synchronized void save(Review entity) {
        if (entity != null) {
            reviews.add(entity);
            flush();
        }
    }

    @Override
    public synchronized Optional<Review> findById(String id) {
        if (id == null) return Optional.empty();
        return reviews.stream().filter(r -> r.getId().equalsIgnoreCase(id)).findFirst();
    }

    public synchronized List<Review> findByGameId(String gameId) {
        if (gameId == null) return Collections.emptyList();
        List<Review> res = new ArrayList<>();
        for (Review r : reviews) {
            if (r.getGameId().equalsIgnoreCase(gameId)) {
                res.add(r);
            }
        }
        return res;
    }

    @Override
    public synchronized List<Review> findAll() {
        return new ArrayList<>(reviews);
    }

    @Override
    public synchronized boolean deleteById(String id) {
        boolean removed = reviews.removeIf(r -> r.getId().equalsIgnoreCase(id));
        if (removed) flush();
        return removed;
    }

    @Override
    public synchronized boolean existsById(String id) {
        return findById(id).isPresent();
    }
}
