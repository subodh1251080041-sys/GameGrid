# 🎮 Game Grid — A Digital Game Purchase Store
> **Course Project:** Object-Oriented Programming in Java (OOPS CP)  
> **Tech Stack:** Java 17+ (Core Java, OOPS, Collections, File I/O, Streams)

---

## 📌 1. Project Overview

**Game Grid** is an enterprise-grade digital game store console platform (inspired by Steam, Epic Games Store, and GOG) built strictly adhering to **Object-Oriented Programming (OOP)** principles in Pure Java.

### Key Highlights:
- **Interactive Console UI**: Aesthetic ANSI colored menus, formatted ASCII data tables, and purchase receipts.
- **Persistent Storage**: All games, user profiles, shopping carts, transactions, coupons, and reviews persist in human-readable CSV files (`data/*.csv`) across app restarts.
- **Auto-Seeding**: Automatically seeds a catalog of popular titles (e.g., *The Witcher 3, Cyberpunk 2077, Elden Ring, Hades II, Baldur's Gate 3*), default test users, promo codes, and reviews on first boot.
- **Zero External Dependencies**: Pure Java Standard Edition.

---

## 🚀 2. Quick Start & Execution

### Option A: Direct Batch Script (Easiest)
In your terminal (PowerShell or CMD) or File Explorer:
```powershell
.\compile.bat
.\run.bat
```

### Option B: PowerShell Manual Execution
```powershell
# 1. Compile all Java sources into bin
javac -encoding UTF-8 -d bin -sourcepath src (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })

# 2. Run the application
java -cp bin com.gamegrid.Main
```

### Option C: Classic Windows CMD Execution
```cmd
# 1. Compile all Java sources into bin
javac -encoding UTF-8 -d bin -sourcepath src src\com\gamegrid\model\*.java src\com\gamegrid\exception\*.java src\com\gamegrid\payment\*.java src\com\gamegrid\repository\*.java src\com\gamegrid\service\*.java src\com\gamegrid\ui\*.java src\com\gamegrid\Main.java

# 2. Run the application
java -cp bin com.gamegrid.Main
```

---

## 🔑 3. Default Demo Accounts

| Role | Username | Password | Features / Pre-seeded Data |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin` | `admin123` | Store catalog management, dynamic game creation via Factory, promotional discount updates, sales revenue analytics. |
| **Customer** | `gamer_john` | `pass123` | Pre-funded with **$150.00**, owns *The Witcher 3* & *Hollow Knight*, wishlisted *Cyberpunk 2077*. |
| **Customer** | `cyber_sam` | `pass123` | Pre-funded with **$80.00**, owns *Elden Ring*. |
| *New Registration* | *(any)* | *(any)* | Instant self-registration with customizable initial deposit. |

---

## 🏷️ 4. Active Promotional Coupon Codes

Test these discount coupons during cart checkout or 1-click purchases:
- `WELCOME20` — 20% OFF (Minimum order: $20.00)
- `SUMMER50` — 50% OFF (Minimum order: $40.00)
- `GRID10` — 10% OFF (No minimum order)
- `LEVELUP15` — 15% OFF (Minimum order: $25.00)

---

## 🏛️ 5. Object-Oriented Programming (OOPS) Mapping

This project was architected specifically to demonstrate mastery over all core OOP principles:

### 1. Encapsulation
- **Data Hiding**: Private instance variables across all models (`Game`, `User`, `Customer`, `Admin`, `Cart`, `Order`).
- **Validation in Accessors**: Setter validation prevents negative prices, discounts exceeding 90%, empty titles, and ratings outside the 1.0–5.0 range.
- **Immutable References**: Unmodifiable collections returned via `Collections.unmodifiableList()` and `Collections.unmodifiableSet()` in `Cart` and `Customer`.

### 2. Inheritance
- **Game Hierarchy**:
  ```
  Game (abstract base class)
   ├── StandardGame       (DRM-free download options)
   ├── DeluxeGame         (Season pass, digital OST/artbook, bundle premium)
   └── EarlyAccessGame    (Alpha/beta build version, roadmap supporter discount)
  ```
- **User Hierarchy**:
  ```
  User (abstract base class)
   ├── Customer           (Wallet balance, game library, wishlist, cart, orders)
   └── Admin              (Department, catalog CRUD, store revenue analytics)
  ```

### 3. Polymorphism
#### A. Runtime Polymorphism (Dynamic Method Dispatch / Overriding):
- `calculateFinalPrice()` in `Game`:
  - `StandardGame`: Applies store promotional discount.
  - `DeluxeGame`: Accounts for bundle content value + promotional discount.
  - `EarlyAccessGame`: Dynamically calculates combined early supporter incentive discount.
- `PaymentStrategy.processPayment(...)`: Dynamically resolved at checkout for:
  - `WalletPaymentStrategy` (deducts funds from customer balance)
  - `CreditCardPaymentStrategy` (validates card details & authorization)
  - `UpiPaymentStrategy` (validates VPA address & instant transfer)

#### B. Compile-Time Polymorphism (Method Overloading):
- `CatalogService` demonstrates overloaded search algorithms:
  - `search(String query)`: Matches title or publisher keyword.
  - `search(Genre genre)`: Matches exact genre category.
  - `search(double minPrice, double maxPrice)`: Matches custom price range.
  - `search(Genre genre, double maxPrice)`: Matches genre within a budget constraint.

### 4. Abstraction & Interfaces
- **`Purchasable`**: Contract for any item eligible for store purchase.
- **`Discountable`**: Interface defining promotional discount capabilities.
- **`PaymentStrategy`**: Abstraction decoupling the checkout service from payment gateways.
- **`Repository<T, ID>`**: Generic data persistence contract decoupling storage mechanisms from business logic.

### 5. Design Patterns
1. **Strategy Pattern**: `PaymentStrategy` with concrete strategies (`WalletPaymentStrategy`, `CreditCardPaymentStrategy`, `UpiPaymentStrategy`).
2. **Factory Pattern**: `GameFactory.createGame(...)` centralizes polymorphic game instantiation based on edition type.
3. **Repository Pattern**: Generic `Repository<T, ID>` handles CSV reading/writing without polluting domain models.

### 6. Robust Custom Exception Hierarchy
```
GameGridException (Base checked exception)
 ├── GameNotFoundException
 ├── InsufficientFundsException
 ├── GameAlreadyOwnedException
 ├── InvalidCouponException
 └── AuthenticationException
```

### 7. Java Collections Framework
- `List<Game>` / `List<CartItem>`: Sequential collections for catalog, cart, and orders.
- `Map<String, Game>` / `Map<String, User>`: O(1) hash lookups by ID and username.
- `Set<String>`: `LinkedHashSet` ensuring duplicate-free game ownership in user libraries.
- `Comparable<Game>`: Enables natural sorting by top ratings.

---

## 📁 6. Project Directory Structure

```
c:\Study material\OOPS CP\
├── src\
│   └── com\gamegrid\
│       ├── Main.java                        # Entry point & guest flow
│       ├── model\
│       │   ├── Game.java                    # Abstract base entity
│       │   ├── StandardGame.java            # Inheritance subclass
│       │   ├── DeluxeGame.java              # Inheritance subclass
│       │   ├── EarlyAccessGame.java         # Inheritance subclass
│       │   ├── GameFactory.java             # Factory Pattern
│       │   ├── Genre.java                   # Genre Enum
│       │   ├── Purchasable.java             # Interface
│       │   ├── Discountable.java            # Interface
│       │   ├── User.java                    # Abstract base entity
│       │   ├── Customer.java                # Customer subclass
│       │   ├── Admin.java                   # Admin subclass
│       │   ├── Cart.java                    # Shopping cart
│       │   ├── CartItem.java                # Cart entry wrapper
│       │   ├── Order.java                   # Transaction receipt
│       │   ├── Coupon.java                  # Promotional discount
│       │   └── Review.java                  # Game review & rating
│       ├── payment\
│       │   ├── PaymentStrategy.java         # Strategy interface
│       │   ├── WalletPaymentStrategy.java   # Concrete strategy
│       │   ├── CreditCardPaymentStrategy.java # Concrete strategy
│       │   └── UpiPaymentStrategy.java      # Concrete strategy
│       ├── repository\
│       │   ├── Repository.java              # Generic repository interface
│       │   ├── GameRepository.java          # CSV game persistence
│       │   ├── UserRepository.java          # CSV user persistence
│       │   ├── OrderRepository.java         # CSV order persistence
│       │   ├── CouponRepository.java        # CSV coupon persistence
│       │   └── ReviewRepository.java        # CSV review persistence
│       ├── service\
│       │   ├── AuthService.java             # Authentication & registration
│       │   ├── CatalogService.java          # Overloaded search & filtering
│       │   ├── CheckoutService.java         # Order processing & payment dispatch
│       │   └── ReviewService.java           # Review & rating management
│       ├── exception\
│       │   ├── GameGridException.java       # Base exception
│       │   ├── GameNotFoundException.java
│       │   ├── InsufficientFundsException.java
│       │   ├── GameAlreadyOwnedException.java
│       │   ├── InvalidCouponException.java
│       │   └── AuthenticationException.java
│       └── ui\
│           ├── ConsoleColors.java           # ANSI color codes
│           ├── ConsoleTable.java            # Formatted ASCII tables
│           ├── InputReader.java             # Crash-proof scanner
│           ├── CustomerView.java            # Customer storefront & library
│           └── AdminView.java               # Administrator console
├── data\                                    # Auto-generated CSV files
│   ├── games.csv
│   ├── users.csv
│   ├── orders.csv
│   ├── coupons.csv
│   └── reviews.csv
├── bin\                                     # Compiled .class binaries
├── compile.bat                              # Build script
├── run.bat                                  # Execution script
└── README.md                                # Project documentation
```
