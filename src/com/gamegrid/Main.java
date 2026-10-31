package com.gamegrid;

import com.gamegrid.exception.AuthenticationException;
import com.gamegrid.model.Admin;
import com.gamegrid.model.Customer;
import com.gamegrid.model.Game;
import com.gamegrid.model.User;
import com.gamegrid.repository.*;
import com.gamegrid.service.*;
import com.gamegrid.ui.*;

import java.io.File;

/**
 * Main Entry Point for Game Grid - A Game Purchase Store.
 * Course Project demonstrating Object-Oriented Programming (OOPS) in Java.
 */
public class Main {
    private static GameRepository gameRepo;
    private static UserRepository userRepo;
    private static OrderRepository orderRepo;
    private static CouponRepository couponRepo;
    private static ReviewRepository reviewRepo;

    private static AuthService authService;
    private static CatalogService catalogService;
    private static CheckoutService checkoutService;
    private static ReviewService reviewService;

    public static void main(String[] args) {
        initializeDependencies();
        runAppLoop();
    }

    private static void initializeDependencies() {
        String dataDir = "data" + File.separator;
        gameRepo = new GameRepository(dataDir + "games.csv");
        userRepo = new UserRepository(dataDir + "users.csv");
        orderRepo = new OrderRepository(dataDir + "orders.csv");
        couponRepo = new CouponRepository(dataDir + "coupons.csv");
        reviewRepo = new ReviewRepository(dataDir + "reviews.csv");

        authService = new AuthService(userRepo);
        catalogService = new CatalogService(gameRepo);
        checkoutService = new CheckoutService(userRepo, orderRepo, couponRepo);
        reviewService = new ReviewService(reviewRepo, gameRepo);
    }

    private static void runAppLoop() {
        boolean running = true;
        while (running) {
            printBanner();
            System.out.println(ConsoleColors.BOLD + "MAIN MENU" + ConsoleColors.RESET);
            System.out.println(" [1] Sign In to Your Account");
            System.out.println(" [2] Register New Gamer Account");
            System.out.println(" [3] Browse Storefront as Guest");
            System.out.println(" [4] View OOP Project Architecture & Viva Guide");
            System.out.println(" [0] Exit");
            System.out.println("-".repeat(60));

            int choice = InputReader.readInt("Select option", 0, 4);
            switch (choice) {
                case 1 -> loginFlow();
                case 2 -> registerFlow();
                case 3 -> guestBrowseFlow();
                case 4 -> showOopArchitectureGuide();
                case 0 -> {
                    System.out.println("\n" + ConsoleColors.BRIGHT_GREEN +
                            "Thank you for visiting Game Grid! Have a great gaming day!" + ConsoleColors.RESET);
                    running = false;
                }
            }
        }
    }

    private static void printBanner() {
        System.out.println("\n" + ConsoleColors.BRIGHT_CYAN + ConsoleColors.BOLD);
        System.out.println("   ____                               ____      _     _ ");
        System.out.println("  / ___| __ _ _ __ ___   ___         / ___|_ __(_) __| |");
        System.out.println(" | |  _ / _` | '_ ` _ \\ / _ \\ _____ | |  _| '__| |/ _` |");
        System.out.println(" | |_| | (_| | | | | | |  __/|_____|| |_| | |  | | (_| |");
        System.out.println("  \\____|\\__,_|_| |_| |_|\\___|        \\____|_|  |_|\\__,_|");
        System.out.println(ConsoleColors.BRIGHT_YELLOW + "     >> NEXT-GEN DIGITAL GAME PURCHASE STORE (OOPS CP) <<" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.DIM + "  Demo Credentials: Customer [gamer_john / pass123] | Admin [admin / admin123]" + ConsoleColors.RESET);
        System.out.println("=".repeat(60));
    }

    private static void loginFlow() {
        System.out.println("\n" + ConsoleColors.BOLD + "=== USER SIGN IN ===" + ConsoleColors.RESET);
        String username = InputReader.readNonEmptyString("Username");
        String password = InputReader.readNonEmptyString("Password");

        try {
            User user = authService.login(username, password);
            System.out.println(ConsoleColors.BRIGHT_GREEN + "\n✓ Login Successful! " + user.getDashboardGreeting() + ConsoleColors.RESET);

            if (user instanceof Customer customer) {
                CustomerView customerView = new CustomerView(customer, catalogService, checkoutService,
                        reviewService, orderRepo, userRepo);
                customerView.run();
            } else if (user instanceof Admin admin) {
                AdminView adminView = new AdminView(admin, catalogService, orderRepo, couponRepo, userRepo);
                adminView.run();
            }
        } catch (AuthenticationException e) {
            System.out.println(ConsoleColors.RED + "Authentication Error: " + e.getMessage() + ConsoleColors.RESET);
            InputReader.pause();
        }
    }

    private static void registerFlow() {
        System.out.println("\n" + ConsoleColors.BOLD + "=== CREATE NEW GAMER ACCOUNT ===" + ConsoleColors.RESET);
        String username = InputReader.readNonEmptyString("Choose a Username");
        String email = InputReader.readNonEmptyString("Enter Email Address");
        String password = InputReader.readNonEmptyString("Create a Password");
        double initialDeposit = InputReader.readDouble("Initial Wallet Deposit Amount (0 for none)", 0.0, 5000.0);

        try {
            Customer customer = authService.registerCustomer(username, email, password, initialDeposit);
            System.out.println(ConsoleColors.BRIGHT_GREEN + "\n✓ Welcome to Game Grid, " + customer.getUsername() + "! Account created successfully." + ConsoleColors.RESET);
            CustomerView customerView = new CustomerView(customer, catalogService, checkoutService,
                    reviewService, orderRepo, userRepo);
            customerView.run();
        } catch (AuthenticationException e) {
            System.out.println(ConsoleColors.RED + "Registration Error: " + e.getMessage() + ConsoleColors.RESET);
            InputReader.pause();
        }
    }

    private static void guestBrowseFlow() {
        System.out.println("\n" + ConsoleColors.BOLD + "=== GUEST STOREFRONT PREVIEW ===" + ConsoleColors.RESET);
        ConsoleTable table = new ConsoleTable("ID", "Game Title", "Genre", "Edition", "Final Price", "Rating");
        for (Game g : catalogService.getAllGames()) {
            table.addRow(
                    g.getId(),
                    g.getTitle(),
                    g.getGenre().name(),
                    g.getDisplayType(),
                    String.format("$%.2f", g.calculateFinalPrice()),
                    String.format("%.1f★", g.getRating())
            );
        }
        table.print();
        System.out.println(ConsoleColors.YELLOW + "Note: Sign in or register an account to add games to your cart, buy, and play!" + ConsoleColors.RESET);
        InputReader.pause();
    }

    private static void showOopArchitectureGuide() {
        System.out.println("\n" + ConsoleColors.BOLD + ConsoleColors.BRIGHT_CYAN +
                "============================================================");
        System.out.println("  GAME GRID: OBJECT-ORIENTED PROGRAMMING (OOPS) ARCHITECTURE");
        System.out.println("============================================================" + ConsoleColors.RESET);
        System.out.println("1. ENCAPSULATION:");
        System.out.println("   • Private attributes in Game, User, Customer, Admin, Order, Cart.");
        System.out.println("   • Invariant business validation inside setters and constructors.");
        System.out.println();
        System.out.println("2. INHERITANCE:");
        System.out.println("   • Base Abstract Class 'Game' extended by:");
        System.out.println("       - StandardGame (standard releases with DRM-free options)");
        System.out.println("       - DeluxeGame (season pass, OST, bonus bundle premium)");
        System.out.println("       - EarlyAccessGame (alpha/beta build roadmap & supporter discount)");
        System.out.println("   • Base Abstract Class 'User' extended by Customer and Admin.");
        System.out.println();
        System.out.println("3. POLYMORPHISM:");
        System.out.println("   • Runtime Polymorphism (Method Overriding):");
        System.out.println("       - calculateFinalPrice() dynamically calculates price per game edition.");
        System.out.println("       - PaymentStrategy.processPayment() executed dynamically across Wallet, Card, UPI.");
        System.out.println("   • Compile-Time Polymorphism (Method Overloading):");
        System.out.println("       - CatalogService.search(String query)");
        System.out.println("       - CatalogService.search(Genre genre)");
        System.out.println("       - CatalogService.search(double min, double max)");
        System.out.println("       - CatalogService.search(Genre genre, double maxPrice)");
        System.out.println();
        System.out.println("4. ABSTRACTION & INTERFACES:");
        System.out.println("   • Purchasable, Discountable, PaymentStrategy, Repository<T, ID>.");
        System.out.println();
        System.out.println("5. DESIGN PATTERNS:");
        System.out.println("   • Strategy Pattern: PaymentStrategy (Wallet, CreditCard, UPI).");
        System.out.println("   • Factory Pattern: GameFactory for dynamic game instantiation.");
        System.out.println("   • Repository Pattern: Generic Repository<T, ID> for persistent CSV data.");
        System.out.println();
        System.out.println("6. PERSISTENCE & FILE I/O:");
        System.out.println("   • CSV storage with auto-seeding in /data (games.csv, users.csv, orders.csv, coupons.csv).");
        System.out.println("============================================================");
        InputReader.pause();
    }
}
