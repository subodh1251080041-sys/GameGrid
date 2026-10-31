package com.gamegrid.repository;

import com.gamegrid.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * CSV-backed repository for Game catalog persistence.
 * Demonstrates Java File I/O, Streams, Collections (Map, List), and Exception Handling.
 */
public class GameRepository implements Repository<Game, String> {
    private final File file;
    private final Map<String, Game> gameMap = new LinkedHashMap<>();

    public GameRepository(String filePath) {
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
        gameMap.clear();
        if (!file.exists() || file.length() == 0) {
            seedDefaultCatalog();
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
                    continue; // Skip CSV header
                }
                Game game = parseCsvLine(line);
                if (game != null) {
                    gameMap.put(game.getId().toUpperCase(), game);
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Error reading games CSV. Initializing fallback catalog: " + e.getMessage());
            seedDefaultCatalog();
            flush();
        }
    }

    @Override
    public synchronized void flush() {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.println("type,id,title,genre,publisher,basePrice,discountPercentage,rating,releaseYear,sizeGb,extra1,extra2,extra3");
            for (Game game : gameMap.values()) {
                writer.println(toCsvLine(game));
            }
        } catch (IOException e) {
            System.err.println("Error saving games to file: " + e.getMessage());
        }
    }

    private String toCsvLine(Game game) {
        String type;
        String extra1 = "";
        String extra2 = "";
        String extra3 = "";

        if (game instanceof DeluxeGame) {
            type = "DELUXE";
            DeluxeGame dg = (DeluxeGame) game;
            extra1 = String.valueOf(dg.isSeasonPassIncluded());
            extra2 = String.valueOf(dg.isSoundtrackIncluded());
            extra3 = String.valueOf(dg.getBundlePremium());
        } else if (game instanceof EarlyAccessGame) {
            type = "EARLY_ACCESS";
            EarlyAccessGame eg = (EarlyAccessGame) game;
            extra1 = eg.getBuildVersion();
            extra2 = String.valueOf(eg.getEarlySupporterDiscount());
        } else if (game instanceof StandardGame) {
            type = "STANDARD";
            StandardGame sg = (StandardGame) game;
            extra1 = String.valueOf(sg.isIncludesDrmFreeCopy());
        } else {
            type = "STANDARD";
        }

        return String.format("%s,\"%s\",\"%s\",%s,\"%s\",%.2f,%.2f,%.2f,%d,%.1f,\"%s\",\"%s\",\"%s\"",
                type,
                escape(game.getId()),
                escape(game.getTitle()),
                game.getGenre().name(),
                escape(game.getPublisher()),
                game.getBasePrice(),
                game.getDiscountPercentage(),
                game.getRating(),
                game.getReleaseYear(),
                game.getSizeGb(),
                escape(extra1),
                escape(extra2),
                escape(extra3));
    }

    private Game parseCsvLine(String line) {
        List<String> tokens = parseCsvRow(line);
        if (tokens.size() < 10) return null;

        try {
            String type = tokens.get(0);
            String id = tokens.get(1);
            String title = tokens.get(2);
            Genre genre = Genre.fromString(tokens.get(3));
            String publisher = tokens.get(4);
            double basePrice = Double.parseDouble(tokens.get(5));
            double discount = Double.parseDouble(tokens.get(6));
            double rating = Double.parseDouble(tokens.get(7));
            int releaseYear = Integer.parseInt(tokens.get(8));
            double sizeGb = Double.parseDouble(tokens.get(9));

            String extra1 = tokens.size() > 10 ? tokens.get(10) : "";
            String extra2 = tokens.size() > 11 ? tokens.get(11) : "";
            String extra3 = tokens.size() > 12 ? tokens.get(12) : "";

            return GameFactory.createGame(type, id, title, genre, publisher, basePrice,
                    discount, rating, releaseYear, sizeGb, extra1, extra2, extra3);
        } catch (Exception e) {
            System.err.println("Skipping malformed CSV line: " + line);
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

    private void seedDefaultCatalog() {
        gameMap.clear();
        addDefault(new StandardGame("G101", "The Witcher 3: Wild Hunt", Genre.RPG, "CD Projekt Red", 39.99, 50.0, 4.9, 2015, 50.0, true));
        addDefault(new DeluxeGame("G102", "Cyberpunk 2077: Phantom Liberty", Genre.RPG, "CD Projekt Red", 59.99, 25.0, 4.7, 2023, 70.0, true, true, 20.0));
        addDefault(new StandardGame("G103", "Elden Ring", Genre.RPG, "FromSoftware", 59.99, 15.0, 4.9, 2022, 60.0, false));
        addDefault(new EarlyAccessGame("G104", "Hades II", Genre.ACTION, "Supergiant Games", 29.99, 0.0, 4.8, 2024, 10.0, "v0.9.1-beta", 10.0));
        addDefault(new DeluxeGame("G105", "Baldur's Gate 3 Collector", Genre.RPG, "Larian Studios", 69.99, 10.0, 5.0, 2023, 122.0, true, true, 15.0));
        addDefault(new StandardGame("G106", "Hollow Knight", Genre.INDIE, "Team Cherry", 14.99, 50.0, 4.9, 2017, 9.0, true));
        addDefault(new StandardGame("G107", "Grand Theft Auto V", Genre.ACTION, "Rockstar Games", 29.99, 60.0, 4.8, 2015, 95.0, false));
        addDefault(new EarlyAccessGame("G108", "No Rest for the Wicked", Genre.ACTION, "Moon Studios", 39.99, 10.0, 4.4, 2024, 35.0, "v0.3-preview", 10.0));
        addDefault(new StandardGame("G109", "Stardew Valley", Genre.SIMULATION, "ConcernedApe", 14.99, 20.0, 4.9, 2016, 1.5, true));
        addDefault(new DeluxeGame("G110", "Red Dead Redemption 2 Deluxe", Genre.ADVENTURE, "Rockstar Games", 59.99, 65.0, 4.9, 2018, 115.0, true, true, 25.0));
    }

    private void addDefault(Game g) {
        gameMap.put(g.getId().toUpperCase(), g);
    }

    @Override
    public synchronized void save(Game entity) {
        if (entity != null) {
            gameMap.put(entity.getId().toUpperCase(), entity);
            flush();
        }
    }

    @Override
    public synchronized Optional<Game> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(gameMap.get(id.toUpperCase()));
    }

    @Override
    public synchronized List<Game> findAll() {
        return new ArrayList<>(gameMap.values());
    }

    @Override
    public synchronized boolean deleteById(String id) {
        if (id == null) return false;
        boolean removed = gameMap.remove(id.toUpperCase()) != null;
        if (removed) {
            flush();
        }
        return removed;
    }

    @Override
    public synchronized boolean existsById(String id) {
        return id != null && gameMap.containsKey(id.toUpperCase());
    }
}
