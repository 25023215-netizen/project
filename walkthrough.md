# Walkthrough - Auction System Core Implementation

I have implemented the core components of the auction system following the Singleton pattern and the OOP requirements specified in `yeucau-BTL.txt`.

## Key Achievements

### 1. Singleton Design Pattern
The `AuctionManager` class was implemented as a Singleton to centrally manage active auctions. This ensures that all parts of the system interact with the same set of running auctions, which is crucial for real-time updates.

```java
// AuctionManager.java snippet
public class AuctionManager {
    private static final AuctionManager instance = new AuctionManager();
    private final Map<Long, Auction> activeAuctions = new ConcurrentHashMap<>();

    private AuctionManager() {}

    public static AuctionManager getInstance() {
        return instance;
    }
}
```

### 2. OOP & Inheritance Hierarchy
I've established a clean inheritance structure for products and users:
- **BaseEntity**: Abstract base class for all entities.
- **Item**: Abstract class for auctionable products.
    - **Electronics**, **Art**, **Vehicle**: Concrete subclasses.
- **Users**: Refactored to extend `BaseEntity`.

### 3. Business Logic (Bidding)
The `AuctionService` handles the core logic for placing bids, ensuring that:
- Bids are only placed on running auctions.
- New bids must be higher than the current price (Requirement 3.1.3).
- Every bid is recorded in `BidTransaction` for history tracking.

## Verification Results

### Singleton Verification
I ran a JUnit test to confirm that `AuctionManager` correctly maintains a single instance.

**Test Output:**
```text
[INFO] Running com.nhom4project.auctionweb.service.AuctionManagerTest
AuctionManager initialized (Singleton)
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## How to use
You can now use `AuctionService` in your controllers to start auctions and process bids. The `AuctionManager` will automatically track active sessions in memory for fast access.
