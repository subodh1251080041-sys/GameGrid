package com.gamegrid.repository;

import com.gamegrid.model.Coupon;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * CSV-backed repository for promotional coupon codes.
 */
public class CouponRepository implements Repository<Coupon, String> {
    private final File file;
    private final Map<String, Coupon> couponMap = new LinkedHashMap<>();

    public CouponRepository(String filePath) {
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
        couponMap.clear();
        if (!file.exists() || file.length() == 0) {
            seedDefaultCoupons();
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
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String code = parts[0].trim().toUpperCase();
                    double discount = Double.parseDouble(parts[1].trim());
                    double minOrder = Double.parseDouble(parts[2].trim());
                    boolean active = Boolean.parseBoolean(parts[3].trim());
                    couponMap.put(code, new Coupon(code, discount, minOrder, active));
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Error reading coupons CSV. Seeding defaults: " + e.getMessage());
            seedDefaultCoupons();
            flush();
        }
    }

    @Override
    public synchronized void flush() {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.println("code,discountPercentage,minOrderValue,active");
            for (Coupon c : couponMap.values()) {
                writer.println(String.format("%s,%.2f,%.2f,%b",
                        c.getCode(), c.getDiscountPercentage(), c.getMinOrderValue(), c.isActive()));
            }
        } catch (IOException e) {
            System.err.println("Error saving coupons to file: " + e.getMessage());
        }
    }

    private void seedDefaultCoupons() {
        couponMap.clear();
        couponMap.put("WELCOME20", new Coupon("WELCOME20", 20.0, 20.0, true));
        couponMap.put("SUMMER50", new Coupon("SUMMER50", 50.0, 50.0, true));
        couponMap.put("GRID10", new Coupon("GRID10", 10.0, 0.0, true));
        couponMap.put("LEVELUP15", new Coupon("LEVELUP15", 15.0, 25.0, true));
    }

    @Override
    public synchronized void save(Coupon entity) {
        if (entity != null) {
            couponMap.put(entity.getCode().toUpperCase(), entity);
            flush();
        }
    }

    @Override
    public synchronized Optional<Coupon> findById(String code) {
        if (code == null) return Optional.empty();
        return Optional.ofNullable(couponMap.get(code.trim().toUpperCase()));
    }

    @Override
    public synchronized List<Coupon> findAll() {
        return new ArrayList<>(couponMap.values());
    }

    @Override
    public synchronized boolean deleteById(String code) {
        if (code == null) return false;
        boolean removed = couponMap.remove(code.trim().toUpperCase()) != null;
        if (removed) flush();
        return removed;
    }

    @Override
    public synchronized boolean existsById(String code) {
        return code != null && couponMap.containsKey(code.trim().toUpperCase());
    }
}
