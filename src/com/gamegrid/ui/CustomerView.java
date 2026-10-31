package com.gamegrid.ui;

import com.gamegrid.exception.GameGridException;
import com.gamegrid.exception.GameNotFoundException;
import com.gamegrid.model.*;
import com.gamegrid.payment.CreditCardPaymentStrategy;
import com.gamegrid.payment.PaymentStrategy;
import com.gamegrid.payment.UpiPaymentStrategy;
import com.gamegrid.payment.WalletPaymentStrategy;
import com.gamegrid.repository.OrderRepository;
import com.gamegrid.repository.UserRepository;
import com.gamegrid.service.CatalogService;
import com.gamegrid.service.CheckoutService;
import com.gamegrid.service.ReviewService;

import java.util.List;
import java.util.Set;

/**
 * Interactive Console View for Customers.
 * Provides storefront browsing, cart management, checkout with Strategy Pattern,
 * library viewing, and reviews.
 */
public class CustomerView {
    private final Customer customer;
    private final CatalogService catalogService;
    private final CheckoutService checkoutService;
    private final ReviewService reviewService;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public CustomerView(Customer customer, CatalogService catalogService,
                        CheckoutService checkoutService, ReviewService reviewService,
                        OrderRepository orderRepository, UserRepository userRepository) {
        this.customer = customer;
        this.catalogService = catalogService;
        this.checkoutService = checkoutService;
        this.reviewService = reviewService;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public void run() {
        boolean running = true;
        while (running) {
            printHeader();
            System.out.println(ConsoleColors.BOLD + "STOREFRONT & DISCOVERY" + ConsoleColors.RESET);
            System.out.println(" [1] Browse All Store Games");
            System.out.println(" [2] Search & Filter Games (By Title, Genre, Price)");
            System.out.println(" [3] Inspect Game Details & Community Reviews");
            System.out.println(" [4] View On-Sale Deals & Specials");
            System.out.println();
            System.out.println(ConsoleColors.BOLD + "MY ACCOUNT & ORDERS" + ConsoleColors.RESET);
            System.out.printf(" [5] Shopping Cart (%d items - $%.2f)\n", customer.getCart().getItemCount(), customer.getCart().calculateSubtotal());
            System.out.printf(" [6] My Game Library (%d titles)\n", customer.getOwnedGameIds().size());
            System.out.printf(" [7] My Wishlist (%d titles)\n", customer.getWishlistGameIds().size());
            System.out.printf(" [8] Digital Wallet & Add Funds ($%.2f)\n", customer.getWalletBalance());
            System.out.println(" [9] Purchase History & Receipts");
            System.out.println(" [10] Write a Verified Game Review");
            System.out.println(" [0] Logout");
            System.out.println("─".repeat(60));

            int choice = InputReader.readInt("Enter menu option", 0, 10);
            switch (choice) {
                case 1 -> browseGames(catalogService.getAllGames(), "GAME GRID STORE CATALOG");
                case 2 -> searchAndFilterMenu();
                case 3 -> inspectGameFlow();
                case 4 -> browseGames(catalogService.getOnSaleGames(), "CURRENT DEALS & SPECIAL PROMOTIONS");
                case 5 -> viewCartAndCheckout();
                case 6 -> viewLibrary();
                case 7 -> viewWishlist();
                case 8 -> manageWallet();
                case 9 -> viewOrderHistory();
                case 10 -> writeReviewFlow();
                case 0 -> {
                    System.out.println(ConsoleColors.GREEN + "Logging out... Have a great gaming session!" + ConsoleColors.RESET);
                    running = false;
                }
            }
        }
    }

    private void printHeader() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println(ConsoleColors.BOLD + ConsoleColors.BRIGHT_CYAN +
                "  >> GAME GRID - GAMER DASHBOARD <<" + ConsoleColors.RESET);
        System.out.println("=".repeat(60));
        System.out.printf(" User: %s%s%s | Wallet Balance: %s$%.2f%s | Library: %d games\n",
                ConsoleColors.BOLD, customer.getUsername(), ConsoleColors.RESET,
                ConsoleColors.BRIGHT_GREEN, customer.getWalletBalance(), ConsoleColors.RESET,
                customer.getOwnedGameIds().size());
        System.out.println("=".repeat(60));
    }

    private void browseGames(List<Game> games, String title) {
        System.out.println("\n" + ConsoleColors.BOLD + ConsoleColors.BRIGHT_YELLOW + "=== " + title + " ===" + ConsoleColors.RESET);
        ConsoleTable table = new ConsoleTable("ID", "Title", "Genre", "Edition", "Price", "Discount", "Rating");
        for (Game g : games) {
            String disc = g.hasDiscount() ? String.format("%.0f%% OFF", g.getDiscountPercentage()) : "-";
            String ownedTag = customer.ownsGame(g.getId()) ? " [OWNED]" : "";
            table.addRow(
                    g.getId(),
                    g.getTitle() + ownedTag,
                    g.getGenre().name(),
                    g.getDisplayType(),
                    String.format("$%.2f", g.calculateFinalPrice()),
                    disc,
                    String.format("%.1f★", g.getRating())
            );
        }
        table.print();

        System.out.println("\nActions: [1] Inspect a Game | [2] Sort by Rating | [3] Sort by Price | [0] Back");
        int subChoice = InputReader.readInt("Choose action", 0, 3);
        if (subChoice == 1) {
            inspectGameFlow();
        } else if (subChoice == 2) {
            browseGames(catalogService.getGamesSortedByRating(), "GAMES SORTED BY TOP RATING");
        } else if (subChoice == 3) {
            boolean asc = InputReader.readBoolean("Sort ascending (lowest price first)?");
            browseGames(catalogService.getGamesSortedByPrice(asc), "GAMES SORTED BY PRICE");
        }
    }

    private void searchAndFilterMenu() {
        System.out.println("\n" + ConsoleColors.BOLD + "--- SEARCH & DISCOVERY ---" + ConsoleColors.RESET);
        System.out.println(" [1] Search by Title / Publisher Keyword");
        System.out.println(" [2] Filter by Genre");
        System.out.println(" [3] Filter by Budget / Price Range");
        System.out.println(" [4] Filter by Genre AND Budget");
        System.out.println(" [0] Back");

        int choice = InputReader.readInt("Choose search mode", 0, 4);
        switch (choice) {
            case 1 -> {
                String query = InputReader.readNonEmptyString("Enter keyword");
                browseGames(catalogService.search(query), "SEARCH RESULTS FOR: " + query);
            }
            case 2 -> {
                Genre genre = promptGenre();
                browseGames(catalogService.search(genre), "GENRE: " + genre.name());
            }
            case 3 -> {
                double min = InputReader.readDouble("Minimum price", 0.0, 1000.0);
                double max = InputReader.readDouble("Maximum price", min, 1000.0);
                browseGames(catalogService.search(min, max), String.format("PRICE BETWEEN $%.2f - $%.2f", min, max));
            }
            case 4 -> {
                Genre genre = promptGenre();
                double max = InputReader.readDouble("Maximum budget", 1.0, 1000.0);
                browseGames(catalogService.search(genre, max), String.format("GENRE %s UNDER $%.2f", genre.name(), max));
            }
        }
    }

    private Genre promptGenre() {
        System.out.println("Select Genre:");
        Genre[] genres = Genre.values();
        for (int i = 0; i < genres.length; i++) {
            System.out.printf(" [%d] %s\n", i + 1, genres[i].name());
        }
        int idx = InputReader.readInt("Enter genre number", 1, genres.length);
        return genres[idx - 1];
    }

    private void inspectGameFlow() {
        String gameId = InputReader.readNonEmptyString("Enter Game ID to inspect (e.g., G101)");
        try {
            Game game = catalogService.getGameById(gameId);
            printGameCard(game);

            System.out.println("\nOptions for " + game.getTitle() + ":");
            System.out.println(" [1] Add to Cart");
            System.out.println(" [2] 1-Click Buy Now");
            System.out.println(" [3] Add to Wishlist");
            System.out.println(" [4] View Community Reviews");
            System.out.println(" [0] Back");

            int opt = InputReader.readInt("Select action", 0, 4);
            switch (opt) {
                case 1 -> {
                    if (customer.ownsGame(game.getId())) {
                        System.out.println(ConsoleColors.YELLOW + "You already own this game!" + ConsoleColors.RESET);
                    } else if (customer.getCart().addItem(game)) {
                        System.out.println(ConsoleColors.GREEN + "✓ '" + game.getTitle() + "' added to shopping cart!" + ConsoleColors.RESET);
                    } else {
                        System.out.println(ConsoleColors.YELLOW + "This game is already in your cart." + ConsoleColors.RESET);
                    }
                    InputReader.pause();
                }
                case 2 -> quickBuyFlow(game);
                case 3 -> {
                    if (customer.ownsGame(game.getId())) {
                        System.out.println(ConsoleColors.YELLOW + "You already own this game, no need to wishlist it!" + ConsoleColors.RESET);
                    } else if (customer.addToWishlist(game.getId())) {
                        userRepository.save(customer);
                        System.out.println(ConsoleColors.GREEN + "✓ Added to your wishlist!" + ConsoleColors.RESET);
                    } else {
                        System.out.println(ConsoleColors.YELLOW + "Game is already in your wishlist." + ConsoleColors.RESET);
                    }
                    InputReader.pause();
                }
                case 4 -> showGameReviews(game.getId(), game.getTitle());
            }
        } catch (GameNotFoundException e) {
            System.out.println(ConsoleColors.RED + e.getMessage() + ConsoleColors.RESET);
            InputReader.pause();
        }
    }

    private void printGameCard(Game game) {
        System.out.println("\n+" + "-".repeat(58) + "+");
        System.out.printf("| %-56s |\n", ConsoleColors.BOLD + ConsoleColors.BRIGHT_WHITE + game.getTitle() + ConsoleColors.RESET);
        System.out.printf("| Edition: %-47s |\n", game.getDisplayType());
        System.out.printf("| Genre: %-15s Publisher: %-26s |\n", game.getGenre().name(), game.getPublisher());
        System.out.printf("| Rating: %-14s Release Year: %-22d |\n", String.format("%.1f / 5.0 *", game.getRating()), game.getReleaseYear());
        System.out.printf("| Download Size: %-41s |\n", String.format("%.1f GB", game.getSizeGb()));
        System.out.println("+" + "-".repeat(58) + "+");
        System.out.printf("| Details: %-47s |\n", game.getDetailsSummary());
        System.out.println("+" + "-".repeat(58) + "+");
        System.out.printf("| Base Price: $%-12.2f Discount: %-23s |\n",
                game.getBasePrice(),
                game.hasDiscount() ? String.format("%.0f%% OFF", game.getDiscountPercentage()) : "None");
        System.out.printf("| %-56s |\n", ConsoleColors.BOLD + ConsoleColors.BRIGHT_GREEN +
                String.format("FINAL STORE PRICE: $%.2f", game.calculateFinalPrice()) + ConsoleColors.RESET);
        System.out.println("+" + "-".repeat(58) + "+");
    }

    private void quickBuyFlow(Game game) {
        if (customer.ownsGame(game.getId())) {
            System.out.println(ConsoleColors.YELLOW + "You already own '" + game.getTitle() + "'!" + ConsoleColors.RESET);
            InputReader.pause();
            return;
        }

        System.out.printf("\nReady to purchase: %s for $%.2f\n", game.getTitle(), game.calculateFinalPrice());
        String coupon = InputReader.readString("Enter promo code (Press Enter to skip)");
        PaymentStrategy strategy = selectPaymentStrategy();

        try {
            Order order = checkoutService.buySingleGame(customer, game, strategy, coupon);
            printReceipt(order);
        } catch (GameGridException e) {
            System.out.println(ConsoleColors.RED + "Purchase failed: " + e.getMessage() + ConsoleColors.RESET);
        }
        InputReader.pause();
    }

    private void viewCartAndCheckout() {
        Cart cart = customer.getCart();
        System.out.println("\n" + ConsoleColors.BOLD + "=== SHOPPING CART ===" + ConsoleColors.RESET);
        if (cart.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "Your cart is currently empty. Browse the store to add games!" + ConsoleColors.RESET);
            InputReader.pause();
            return;
        }

        ConsoleTable table = new ConsoleTable("Item #", "ID", "Game Title", "Price");
        int idx = 1;
        for (CartItem item : cart.getItems()) {
            table.addRow(
                    String.valueOf(idx++),
                    item.getGame().getId(),
                    item.getGame().getTitle(),
                    String.format("$%.2f", item.getGame().calculateFinalPrice())
            );
        }
        table.print();
        System.out.printf(ConsoleColors.BOLD + "\nSubtotal (%d items): $%.2f\n" + ConsoleColors.RESET,
                cart.getItemCount(), cart.calculateSubtotal());

        System.out.println("\nCart Actions: [1] Proceed to Checkout | [2] Remove Item | [3] Clear Cart | [0] Back");
        int opt = InputReader.readInt("Select action", 0, 3);
        switch (opt) {
            case 1 -> executeCheckout();
            case 2 -> {
                String idToRemove = InputReader.readNonEmptyString("Enter Game ID to remove from cart");
                if (cart.removeItem(idToRemove)) {
                    System.out.println(ConsoleColors.GREEN + "Item removed from cart." + ConsoleColors.RESET);
                } else {
                    System.out.println(ConsoleColors.RED + "Game ID not found in cart." + ConsoleColors.RESET);
                }
                InputReader.pause();
            }
            case 3 -> {
                cart.clear();
                System.out.println(ConsoleColors.GREEN + "Cart emptied." + ConsoleColors.RESET);
                InputReader.pause();
            }
        }
    }

    private void executeCheckout() {
        System.out.println("\n" + ConsoleColors.BOLD + "--- CHECKOUT & PAYMENT ---" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.DIM + "Available promo coupons: WELCOME20 (20% off), SUMMER50 (50% off), GRID10 (10% off)" + ConsoleColors.RESET);
        String coupon = InputReader.readString("Enter promotional coupon code (or press Enter to skip)");

        PaymentStrategy strategy = selectPaymentStrategy();

        try {
            Order order = checkoutService.checkoutCart(customer, strategy, coupon);
            printReceipt(order);
        } catch (GameGridException e) {
            System.out.println(ConsoleColors.RED + "Checkout failed: " + e.getMessage() + ConsoleColors.RESET);
        }
        InputReader.pause();
    }

    private PaymentStrategy selectPaymentStrategy() {
        System.out.println("\nSelect Payment Method (Strategy Pattern):");
        System.out.printf(" [1] Digital Wallet (Current Balance: $%.2f)\n", customer.getWalletBalance());
        System.out.println(" [2] Credit / Debit Card");
        System.out.println(" [3] UPI Transfer (Instant)");

        int choice = InputReader.readInt("Select payment option", 1, 3);
        switch (choice) {
            case 2 -> {
                String card = InputReader.readNonEmptyString("Enter 16-digit Card Number");
                String holder = InputReader.readNonEmptyString("Enter Cardholder Name");
                String exp = InputReader.readNonEmptyString("Enter Expiration Date (MM/YY)");
                return new CreditCardPaymentStrategy(card, holder, exp);
            }
            case 3 -> {
                String upi = InputReader.readNonEmptyString("Enter UPI ID (e.g. gamer@okaxis)");
                return new UpiPaymentStrategy(upi);
            }
            default -> {
                return new WalletPaymentStrategy();
            }
        }
    }

    private void printReceipt(Order order) {
        System.out.println("\n+" + "=".repeat(58) + "+");
        System.out.println("|" + ConsoleColors.BOLD + ConsoleColors.BRIGHT_GREEN + "             *** PURCHASE SUCCESSFUL - GAME GRID RECEIPT ***          " + ConsoleColors.RESET + "|");
        System.out.println("+" + "=".repeat(58) + "+");
        System.out.printf("| Order ID: %-46s |\n", order.getOrderId());
        System.out.printf("| Date & Time: %-43s |\n", order.getTimestamp());
        System.out.printf("| Customer: %-46s |\n", order.getCustomerUsername());
        System.out.printf("| Payment Method: %-40s |\n", order.getPaymentMethod());
        System.out.println("+" + "-".repeat(58) + "+");
        System.out.println("| Purchased Titles:                                        |");
        for (String title : order.getGameTitles()) {
            System.out.printf("|  * %-53s |\n", title);
        }
        System.out.println("+" + "-".repeat(58) + "+");
        System.out.printf("| Subtotal:                                      $%-8.2f |\n", order.getSubtotal());
        if (order.getDiscountAmount() > 0) {
            System.out.printf("| Coupon Savings:                               -$%-8.2f |\n", order.getDiscountAmount());
        }
        System.out.printf("| %-47s $%-8.2f |\n",
                ConsoleColors.BOLD + "TOTAL PAID:" + ConsoleColors.RESET, order.getFinalTotal());
        System.out.println("+" + "=".repeat(58) + "+");
        System.out.println(ConsoleColors.GREEN + "All games have been permanently added to your Game Grid Library!" + ConsoleColors.RESET);
    }

    private void viewLibrary() {
        System.out.println("\n" + ConsoleColors.BOLD + "=== MY GAME LIBRARY ===" + ConsoleColors.RESET);
        Set<String> ownedIds = customer.getOwnedGameIds();
        if (ownedIds.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "You don't own any games yet. Explore the store!" + ConsoleColors.RESET);
            InputReader.pause();
            return;
        }

        ConsoleTable table = new ConsoleTable("ID", "Game Title", "Genre", "Size", "Status");
        for (String id : ownedIds) {
            try {
                Game g = catalogService.getGameById(id);
                table.addRow(g.getId(), g.getTitle(), g.getGenre().name(), String.format("%.1f GB", g.getSizeGb()), "Ready to Play");
            } catch (GameNotFoundException ignored) {}
        }
        table.print();

        System.out.println("\nActions: [1] Launch/Play a Game | [2] Write a Review | [0] Back");
        int opt = InputReader.readInt("Select action", 0, 2);
        if (opt == 1) {
            String playId = InputReader.readNonEmptyString("Enter Game ID to play");
            if (customer.ownsGame(playId)) {
                try {
                    Game g = catalogService.getGameById(playId);
                    System.out.println(ConsoleColors.BRIGHT_GREEN + "\n🚀 Launching " + g.getTitle() + "..." + ConsoleColors.RESET);
                    System.out.println(ConsoleColors.CYAN + "Connecting to Game Grid Cloud Network... Synchronizing save states..." + ConsoleColors.RESET);
                    System.out.println(ConsoleColors.GREEN + "Enjoy your game!" + ConsoleColors.RESET);
                } catch (GameNotFoundException ignored) {}
            } else {
                System.out.println(ConsoleColors.RED + "You do not own this game." + ConsoleColors.RESET);
            }
            InputReader.pause();
        } else if (opt == 2) {
            writeReviewFlow();
        }
    }

    private void viewWishlist() {
        System.out.println("\n" + ConsoleColors.BOLD + "=== MY WISHLIST ===" + ConsoleColors.RESET);
        Set<String> wishlistIds = customer.getWishlistGameIds();
        if (wishlistIds.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "Your wishlist is empty." + ConsoleColors.RESET);
            InputReader.pause();
            return;
        }

        ConsoleTable table = new ConsoleTable("ID", "Game Title", "Genre", "Price", "Discount");
        for (String id : wishlistIds) {
            try {
                Game g = catalogService.getGameById(id);
                String disc = g.hasDiscount() ? String.format("%.0f%% OFF", g.getDiscountPercentage()) : "-";
                table.addRow(g.getId(), g.getTitle(), g.getGenre().name(), String.format("$%.2f", g.calculateFinalPrice()), disc);
            } catch (GameNotFoundException ignored) {}
        }
        table.print();

        System.out.println("\nActions: [1] Move Game to Cart | [2] Remove from Wishlist | [0] Back");
        int opt = InputReader.readInt("Select action", 0, 2);
        if (opt == 1) {
            String id = InputReader.readNonEmptyString("Enter Game ID to move to cart");
            try {
                Game g = catalogService.getGameById(id);
                if (customer.getCart().addItem(g)) {
                    customer.removeFromWishlist(id);
                    userRepository.save(customer);
                    System.out.println(ConsoleColors.GREEN + "✓ Moved '" + g.getTitle() + "' to shopping cart!" + ConsoleColors.RESET);
                }
            } catch (GameNotFoundException e) {
                System.out.println(ConsoleColors.RED + e.getMessage() + ConsoleColors.RESET);
            }
            InputReader.pause();
        } else if (opt == 2) {
            String id = InputReader.readNonEmptyString("Enter Game ID to remove");
            if (customer.removeFromWishlist(id)) {
                userRepository.save(customer);
                System.out.println(ConsoleColors.GREEN + "Removed from wishlist." + ConsoleColors.RESET);
            } else {
                System.out.println(ConsoleColors.RED + "Game ID not found in wishlist." + ConsoleColors.RESET);
            }
            InputReader.pause();
        }
    }

    private void manageWallet() {
        System.out.println("\n" + ConsoleColors.BOLD + "=== DIGITAL WALLET ===" + ConsoleColors.RESET);
        System.out.printf("Current Balance: %s$%.2f%s\n\n", ConsoleColors.BRIGHT_GREEN, customer.getWalletBalance(), ConsoleColors.RESET);
        System.out.println("Quick Deposit Amounts:");
        System.out.println(" [1] Add $10.00");
        System.out.println(" [2] Add $25.00");
        System.out.println(" [3] Add $50.00");
        System.out.println(" [4] Add $100.00");
        System.out.println(" [5] Custom Deposit Amount");
        System.out.println(" [0] Back");

        int choice = InputReader.readInt("Select option", 0, 5);
        double deposit = switch (choice) {
            case 1 -> 10.0;
            case 2 -> 25.0;
            case 3 -> 50.0;
            case 4 -> 100.0;
            case 5 -> InputReader.readDouble("Enter custom deposit amount", 1.0, 5000.0);
            default -> 0.0;
        };

        if (deposit > 0) {
            customer.addFunds(deposit);
            userRepository.save(customer);
            System.out.printf(ConsoleColors.GREEN + "✓ Successfully added $%.2f to your wallet! New Balance: $%.2f\n" + ConsoleColors.RESET,
                    deposit, customer.getWalletBalance());
            InputReader.pause();
        }
    }

    private void viewOrderHistory() {
        System.out.println("\n" + ConsoleColors.BOLD + "=== PURCHASE HISTORY & RECEIPTS ===" + ConsoleColors.RESET);
        List<Order> orders = orderRepository.findByCustomerId(customer.getId());
        if (orders.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "No past orders found." + ConsoleColors.RESET);
            InputReader.pause();
            return;
        }

        ConsoleTable table = new ConsoleTable("Order ID", "Date", "Games", "Total Paid", "Payment Method");
        for (Order o : orders) {
            table.addRow(
                    o.getOrderId(),
                    o.getTimestamp(),
                    String.join(", ", o.getGameTitles()),
                    String.format("$%.2f", o.getFinalTotal()),
                    o.getPaymentMethod()
            );
        }
        table.print();
        InputReader.pause();
    }

    private void writeReviewFlow() {
        System.out.println("\n" + ConsoleColors.BOLD + "=== WRITE A VERIFIED GAME REVIEW ===" + ConsoleColors.RESET);
        String gameId = InputReader.readNonEmptyString("Enter Game ID you wish to review");

        if (!customer.ownsGame(gameId)) {
            System.out.println(ConsoleColors.RED + "Error: Verified purchase required. You can only review games in your library." + ConsoleColors.RESET);
            InputReader.pause();
            return;
        }

        int rating = InputReader.readInt("Enter your rating (1 to 5 stars)", 1, 5);
        String comment = InputReader.readNonEmptyString("Enter your review comment");

        try {
            Review review = reviewService.addReview(customer, gameId, rating, comment);
            System.out.println(ConsoleColors.GREEN + "✓ Review posted successfully: " + review.toString() + ConsoleColors.RESET);
        } catch (GameGridException e) {
            System.out.println(ConsoleColors.RED + e.getMessage() + ConsoleColors.RESET);
        }
        InputReader.pause();
    }

    private void showGameReviews(String gameId, String gameTitle) {
        System.out.println("\n" + ConsoleColors.BOLD + "Community Reviews for " + gameTitle + ":" + ConsoleColors.RESET);
        List<Review> reviews = reviewService.getReviewsForGame(gameId);
        if (reviews.isEmpty()) {
            System.out.println(ConsoleColors.DIM + "No reviews submitted for this title yet. Be the first to review!" + ConsoleColors.RESET);
        } else {
            ConsoleTable table = new ConsoleTable("Review ID", "User", "Rating", "Review", "Date");
            for (Review r : reviews) {
                table.addRow(r.getId(), r.getUsername(), r.getRating() + "★", r.getComment(), r.getTimestamp());
            }
            table.print();
        }
        InputReader.pause();
    }
}
