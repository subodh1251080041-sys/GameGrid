# 🎓 Game Grid — OOPS Project Viva & Defense Guide

Use this reference sheet to ace project presentations, vivas, and professor evaluations for **Game Grid**.

---

### Q1: Where have you demonstrated the 4 Core Pillars of OOP?

| OOP Pillar | Project Implementation & Code References |
| :--- | :--- |
| **Encapsulation** | All fields in `Game.java`, `User.java`, `Customer.java`, `Cart.java`, and `Order.java` are `private`. Access is strictly controlled via public getters and setters with validation (e.g. `Game.setDiscountPercentage()` restricts discounts between 0% and 90%, preventing invalid business states). |
| **Inheritance** | **1. Game Hierarchy:** Abstract `Game` is inherited by `StandardGame`, `DeluxeGame`, and `EarlyAccessGame`.<br>**2. User Hierarchy:** Abstract `User` is inherited by `Customer` and `Admin`. |
| **Polymorphism** | **1. Runtime Polymorphism (Overriding):** `calculateFinalPrice()` calculates prices differently depending on the runtime type of `Game`. `PaymentStrategy.processPayment()` executes the appropriate logic whether paying via Wallet, Card, or UPI.<br>**2. Compile-Time Polymorphism (Overloading):** `CatalogService` provides 4 overloaded `search(...)` methods for query, genre, price range, and composite filters. |
| **Abstraction** | Abstract classes (`Game`, `User`) define core contracts without exposing implementation specifics. Interfaces (`Purchasable`, `Discountable`, `PaymentStrategy`, `Repository<T, ID>`) separate behavior definitions from concrete implementations. |

---

### Q2: What Design Patterns have you used and why?

1. **Strategy Pattern (`com.gamegrid.payment`)**:
   - *Why:* Decouples checkout and payment execution.
   - *Code:* `PaymentStrategy` interface implemented by `WalletPaymentStrategy`, `CreditCardPaymentStrategy`, and `UpiPaymentStrategy`. Adding Apple Pay or PayPal in the future requires only creating a new class without modifying `CheckoutService`.
2. **Factory Pattern (`com.gamegrid.model.GameFactory`)**:
   - *Why:* Centralizes the logic of creating the appropriate subclass (`StandardGame`, `DeluxeGame`, or `EarlyAccessGame`) based on a type string and parameters, keeping instantiation logic away from the UI and CSV parsers.
3. **Repository Pattern (`com.gamegrid.repository`)**:
   - *Why:* Separates data storage (CSV file reading and writing) from business services (`CatalogService`, `CheckoutService`).

---

### Q3: How do you prevent a user from buying the same digital game twice?

- In `Customer.java`, owned games are tracked in a `Set<String> ownedGameIds` (`LinkedHashSet`).
- In `Cart.java`, `addItem()` checks `containsGame(game.getId())`.
- In `CheckoutService.java`, before processing payment, it verifies `customer.ownsGame(item.getGame().getId())`. If already owned, it immediately throws `GameAlreadyOwnedException` and aborts the transaction before deducting any funds.

---

### Q4: How is data persistence handled?

- Data is persisted via human-readable CSV files in the `data/` directory (`games.csv`, `users.csv`, `orders.csv`, `coupons.csv`, `reviews.csv`).
- On application boot, each repository (`GameRepository`, `UserRepository`, etc.) checks if the file exists. If missing or empty, it automatically seeds realistic demo data and flushes to disk.
- Whenever an order is completed, a game added, or a wallet funded, the repository automatically calls `flush()` to write changes to disk.

---

### Q5: What custom exceptions did you create?

All exceptions extend the base checked exception `GameGridException`:
1. `GameNotFoundException` — Thrown when an invalid Game ID is queried.
2. `InsufficientFundsException` — Thrown when wallet balance is lower than the order total.
3. `GameAlreadyOwnedException` — Thrown when attempting to buy a game already in the user's library.
4. `InvalidCouponException` — Thrown when a promo code is invalid, disabled, or doesn't meet minimum order value.
5. `AuthenticationException` — Thrown on failed login or invalid registration details.

---

### Q6: How did you use the Java Collections Framework?

- **`List<Game>` / `List<CartItem>`**: Sequential ordered collections for browsing and checkout.
- **`Map<String, Game>` / `Map<String, User>`**: Hash tables providing $O(1)$ lookup time by Game ID and username.
- **`Set<String>`**: Ensures unique ownership of games per user account.
- **`Comparable<Game>`**: Implemented by `Game` to allow natural sorting by rating descending.
