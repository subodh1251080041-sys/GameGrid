package com.gamegrid.ui;

import com.gamegrid.exception.GameNotFoundException;
import com.gamegrid.model.*;
import com.gamegrid.repository.CouponRepository;
import com.gamegrid.repository.OrderRepository;
import com.gamegrid.repository.UserRepository;
import com.gamegrid.service.CatalogService;

import java.util.List;

/**
 * Interactive Console View for Administrators.
 * Enables store catalog maintenance, game creation via GameFactory,
 * discount updates, coupon configuration, and financial sales analytics.
 */
public class AdminView {
    private final Admin admin;
    private final CatalogService catalogService;
    private final OrderRepository orderRepository;
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;

    public AdminView(Admin admin, CatalogService catalogService, OrderRepository orderRepository,
                     CouponRepository couponRepository, UserRepository userRepository) {
        this.admin = admin;
        this.catalogService = catalogService;
        this.orderRepository = orderRepository;
        this.couponRepository = couponRepository;
        this.userRepository = userRepository;
    }

    public void run() {
        boolean running = true;
        while (running) {
            printHeader();
            System.out.println(ConsoleColors.BOLD + "CATALOG MANAGEMENT" + ConsoleColors.RESET);
            System.out.println(" [1] View Complete Game Inventory");
            System.out.println(" [2] Add New Game (Factory Pattern: Standard / Deluxe / Early Access)");
            System.out.println(" [3] Update Game Discount / Put on Sale");
            System.out.println(" [4] Remove Game from Catalog");
            System.out.println();
            System.out.println(ConsoleColors.BOLD + "STORE OPERATIONS & ANALYTICS" + ConsoleColors.RESET);
            System.out.println(" [5] Store Sales Analytics & Revenue Report");
            System.out.println(" [6] Manage Promotional Discount Coupons");
            System.out.println(" [7] View Registered Store Users");
            System.out.println(" [0] Logout");
            System.out.println("─".repeat(60));

            int choice = InputReader.readInt("Enter menu option", 0, 7);
            switch (choice) {
                case 1 -> viewAllInventory();
                case 2 -> addNewGameFlow();
                case 3 -> updateDiscountFlow();
                case 4 -> removeGameFlow();
                case 5 -> viewSalesReport();
                case 6 -> manageCouponsFlow();
                case 7 -> viewUsersFlow();
                case 0 -> {
                    System.out.println(ConsoleColors.GREEN + "Admin session ended. Returning to main menu..." + ConsoleColors.RESET);
                    running = false;
                }
            }
        }
    }

    private void printHeader() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println(ConsoleColors.BOLD + ConsoleColors.BRIGHT_MAGENTA +
                "  >> GAME GRID - ADMINISTRATOR MANAGEMENT PANEL <<" + ConsoleColors.RESET);
        System.out.println("=".repeat(60));
        System.out.printf(" Admin: %s | Department: %s | Total Inventory: %d games\n",
                admin.getUsername(), admin.getDepartment(), catalogService.getAllGames().size());
        System.out.println("=".repeat(60));
    }

    private void viewAllInventory() {
        System.out.println("\n" + ConsoleColors.BOLD + "=== COMPLETE GAME INVENTORY ===" + ConsoleColors.RESET);
        ConsoleTable table = new ConsoleTable("ID", "Title", "Genre", "Edition", "Base Price", "Discount", "Final Price", "Rating");
        for (Game g : catalogService.getAllGames()) {
            table.addRow(
                    g.getId(),
                    g.getTitle(),
                    g.getGenre().name(),
                    g.getDisplayType(),
                    String.format("$%.2f", g.getBasePrice()),
                    g.hasDiscount() ? String.format("%.0f%%", g.getDiscountPercentage()) : "-",
                    String.format("$%.2f", g.calculateFinalPrice()),
                    String.format("%.1f★", g.getRating())
            );
        }
        table.print();
        InputReader.pause();
    }

    private void addNewGameFlow() {
        System.out.println("\n" + ConsoleColors.BOLD + "=== ADD NEW GAME TO GAME GRID ===" + ConsoleColors.RESET);
        System.out.println("Select Game Edition Type (Factory Pattern):");
        System.out.println(" [1] Standard Edition");
        System.out.println(" [2] Deluxe Edition (Season Pass, Soundtrack, Digital Goods)");
        System.out.println(" [3] Early Access Edition (Alpha/Beta Build, Early Supporter Perk)");

        int typeChoice = InputReader.readInt("Select edition type", 1, 3);
        String type = switch (typeChoice) {
            case 2 -> "DELUXE";
            case 3 -> "EARLY_ACCESS";
            default -> "STANDARD";
        };

        String id = InputReader.readNonEmptyString("Enter unique Game ID (e.g., G111)");
        String title = InputReader.readNonEmptyString("Enter Game Title");

        System.out.println("Select Genre:");
        Genre[] genres = Genre.values();
        for (int i = 0; i < genres.length; i++) {
            System.out.printf(" [%d] %s\n", i + 1, genres[i].name());
        }
        int gIdx = InputReader.readInt("Select genre", 1, genres.length);
        Genre genre = genres[gIdx - 1];

        String publisher = InputReader.readNonEmptyString("Enter Publisher / Studio name");
        double basePrice = InputReader.readDouble("Enter Base Price", 0.0, 999.99);
        double discount = InputReader.readDouble("Enter Launch Discount % (0 for none)", 0.0, 90.0);
        double rating = InputReader.readDouble("Initial Review Rating (1.0 to 5.0)", 1.0, 5.0);
        int year = InputReader.readInt("Release Year", 1990, 2030);
        double sizeGb = InputReader.readDouble("Download Size (GB)", 0.1, 500.0);

        String extra1 = "";
        String extra2 = "";
        String extra3 = "";

        if ("DELUXE".equals(type)) {
            boolean seasonPass = InputReader.readBoolean("Include Season Pass?");
            boolean ost = InputReader.readBoolean("Include Digital Soundtrack & Artbook?");
            double premium = InputReader.readDouble("Bonus Bundle Premium Value", 5.0, 100.0);
            extra1 = String.valueOf(seasonPass);
            extra2 = String.valueOf(ost);
            extra3 = String.valueOf(premium);
        } else if ("EARLY_ACCESS".equals(type)) {
            extra1 = InputReader.readNonEmptyString("Enter Current Build Version (e.g., v0.4.2)");
            double earlyDiscount = InputReader.readDouble("Early Supporter Incentive Discount %", 0.0, 25.0);
            extra2 = String.valueOf(earlyDiscount);
        } else {
            boolean drmFree = InputReader.readBoolean("Offer DRM-Free download copy?");
            extra1 = String.valueOf(drmFree);
        }

        // Factory Pattern Call
        Game newGame = GameFactory.createGame(type, id, title, genre, publisher,
                basePrice, discount, rating, year, sizeGb, extra1, extra2, extra3);

        catalogService.addGame(newGame);
        System.out.println(ConsoleColors.GREEN + "\n✓ Successfully created and published '" + newGame.getTitle() + "' to the store catalog!" + ConsoleColors.RESET);
        InputReader.pause();
    }

    private void updateDiscountFlow() {
        System.out.println("\n" + ConsoleColors.BOLD + "=== UPDATE GAME DISCOUNT / SALE PROMOTION ===" + ConsoleColors.RESET);
        String gameId = InputReader.readNonEmptyString("Enter Game ID to update discount");
        try {
            Game game = catalogService.getGameById(gameId);
            System.out.printf("Current: %s | Base Price: $%.2f | Current Discount: %.0f%%\n",
                    game.getTitle(), game.getBasePrice(), game.getDiscountPercentage());
            double newDiscount = InputReader.readDouble("Enter new discount percentage (0 to 90%)", 0.0, 90.0);
            catalogService.updateDiscount(gameId, newDiscount);
            System.out.printf(ConsoleColors.GREEN + "✓ Discount updated! New Final Price for '%s': $%.2f\n" + ConsoleColors.RESET,
                    game.getTitle(), game.calculateFinalPrice());
        } catch (GameNotFoundException e) {
            System.out.println(ConsoleColors.RED + e.getMessage() + ConsoleColors.RESET);
        }
        InputReader.pause();
    }

    private void removeGameFlow() {
        System.out.println("\n" + ConsoleColors.BOLD + "=== REMOVE GAME FROM CATALOG ===" + ConsoleColors.RESET);
        String gameId = InputReader.readNonEmptyString("Enter Game ID to remove");
        boolean confirm = InputReader.readBoolean("Are you sure you want to permanently remove this title from the store?");
        if (confirm) {
            if (catalogService.removeGame(gameId)) {
                System.out.println(ConsoleColors.GREEN + "✓ Game successfully removed from catalog." + ConsoleColors.RESET);
            } else {
                System.out.println(ConsoleColors.RED + "Game ID not found." + ConsoleColors.RESET);
            }
        }
        InputReader.pause();
    }

    private void viewSalesReport() {
        System.out.println("\n" + ConsoleColors.BOLD + "=== STORE SALES & FINANCIAL REPORT ===" + ConsoleColors.RESET);
        List<Order> orders = orderRepository.findAll();

        double totalRevenue = 0.0;
        double totalDiscountsGiven = 0.0;
        int totalGamesPurchased = 0;

        for (Order o : orders) {
            totalRevenue += o.getFinalTotal();
            totalDiscountsGiven += o.getDiscountAmount();
            totalGamesPurchased += o.getGameTitles().size();
        }

        System.out.println("+" + "-".repeat(50) + "+");
        System.out.printf("| Total Orders Processed:     %-20d |\n", orders.size());
        System.out.printf("| Total Games Sold:           %-20d |\n", totalGamesPurchased);
        System.out.printf("| Gross Lifetime Revenue:     $%-19.2f |\n", totalRevenue);
        System.out.printf("| Total Promotional Savings:  $%-19.2f |\n", totalDiscountsGiven);
        double aov = orders.isEmpty() ? 0.0 : totalRevenue / orders.size();
        System.out.printf("| Average Order Value (AOV):  $%-19.2f |\n", aov);
        System.out.println("+" + "-".repeat(50) + "+");

        System.out.println("\nRecent Transaction Log:");
        ConsoleTable table = new ConsoleTable("Order ID", "Customer", "Date", "Games", "Total", "Payment Method");
        for (Order o : orders) {
            table.addRow(
                    o.getOrderId(),
                    o.getCustomerUsername(),
                    o.getTimestamp(),
                    String.valueOf(o.getGameTitles().size()),
                    String.format("$%.2f", o.getFinalTotal()),
                    o.getPaymentMethod()
            );
        }
        table.print();
        InputReader.pause();
    }

    private void manageCouponsFlow() {
        System.out.println("\n" + ConsoleColors.BOLD + "=== PROMOTIONAL COUPONS ===" + ConsoleColors.RESET);
        ConsoleTable table = new ConsoleTable("Code", "Discount %", "Min Order", "Status");
        for (Coupon c : couponRepository.findAll()) {
            table.addRow(
                    c.getCode(),
                    String.format("%.0f%%", c.getDiscountPercentage()),
                    String.format("$%.2f", c.getMinOrderValue()),
                    c.isActive() ? "ACTIVE" : "DISABLED"
            );
        }
        table.print();

        System.out.println("\nActions: [1] Add New Coupon | [2] Toggle Coupon Active Status | [0] Back");
        int opt = InputReader.readInt("Select action", 0, 2);
        if (opt == 1) {
            String code = InputReader.readNonEmptyString("Enter Coupon Code (e.g., WINTER25)").toUpperCase();
            double discount = InputReader.readDouble("Discount % (1 to 80%)", 1.0, 80.0);
            double minOrder = InputReader.readDouble("Minimum Order Value ($)", 0.0, 500.0);
            Coupon coupon = new Coupon(code, discount, minOrder, true);
            couponRepository.save(coupon);
            System.out.println(ConsoleColors.GREEN + "✓ Coupon saved and active!" + ConsoleColors.RESET);
            InputReader.pause();
        } else if (opt == 2) {
            String code = InputReader.readNonEmptyString("Enter Coupon Code to toggle");
            var optCoupon = couponRepository.findById(code);
            if (optCoupon.isPresent()) {
                Coupon c = optCoupon.get();
                c.setActive(!c.isActive());
                couponRepository.save(c);
                System.out.printf(ConsoleColors.GREEN + "✓ Coupon %s is now %s.\n" + ConsoleColors.RESET,
                        c.getCode(), c.isActive() ? "ACTIVE" : "DISABLED");
            } else {
                System.out.println(ConsoleColors.RED + "Coupon code not found." + ConsoleColors.RESET);
            }
            InputReader.pause();
        }
    }

    private void viewUsersFlow() {
        System.out.println("\n" + ConsoleColors.BOLD + "=== REGISTERED STORE USERS ===" + ConsoleColors.RESET);
        ConsoleTable table = new ConsoleTable("ID", "Username", "Role", "Email", "Details");
        for (User u : userRepository.findAll()) {
            String details = (u instanceof Customer c)
                    ? String.format("Wallet: $%.2f | %d games", c.getWalletBalance(), c.getOwnedGameIds().size())
                    : (u instanceof Admin a) ? "Dept: " + a.getDepartment() : "";
            table.addRow(u.getId(), u.getUsername(), u.getRole(), u.getEmail(), details);
        }
        table.print();
        InputReader.pause();
    }
}
