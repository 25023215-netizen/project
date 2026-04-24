# Implementation Plan - Auction System Completion (Singleton & OOP)

The goal is to complete the basic online auction system as requested in `yeucau-BTL.txt`, specifically focusing on the Singleton pattern and the suggested OOP structure.

## User Review Required

> [!IMPORTANT]
> The system is currently using Spring Boot. While Spring managed beans are singletons by default, I will implement an explicit Singleton pattern for the `AuctionManager` as requested in the requirements to demonstrate the pattern.

## Proposed Changes

### Core OOP Structure (Models)

I will implement the missing entities following the inheritance hierarchy suggested in the requirements.

#### [NEW] [BaseEntity.java](file:///d:/BTL/auctionweb/src/main/java/com/nhom4project/auctionweb/data/model/BaseEntity.java)
Abstract base class for all entities, providing the ID field.

#### [NEW] [Item.java](file:///d:/BTL/auctionweb/src/main/java/com/nhom4project/auctionweb/data/model/Item.java)
Abstract class representing an auction item.

#### [NEW] [Electronics.java](file:///d:/BTL/auctionweb/src/main/java/com/nhom4project/auctionweb/data/model/Electronics.java), [Art.java](file:///d:/BTL/auctionweb/src/main/java/com/nhom4project/auctionweb/data/model/Art.java), [Vehicle.java](file:///d:/BTL/auctionweb/src/main/java/com/nhom4project/auctionweb/data/model/Vehicle.java)
Concrete subclasses of `Item`.

#### [NEW] [Auction.java](file:///d:/BTL/auctionweb/src/main/java/com/nhom4project/auctionweb/data/model/Auction.java)
Manages the auction process for an item.

#### [NEW] [BidTransaction.java](file:///d:/BTL/auctionweb/src/main/java/com/nhom4project/auctionweb/data/model/BidTransaction.java)
Records individual bids.

### Singleton Implementation

#### [NEW] [AuctionManager.java](file:///d:/BTL/auctionweb/src/main/java/com/nhom4project/auctionweb/service/AuctionManager.java)
A Singleton class to manage the lifecycle of auctions (start, end, track active auctions).

### Data Access & Logic

#### [NEW] Repositories
- `ItemRepository.java`
- `AuctionRepository.java`
- `BidTransactionRepository.java`

#### [NEW] Services
- `AuctionService.java`: Logic for placing bids, checking validity, etc.

## Verification Plan

### Automated Tests
- I will add a unit test to verify that `AuctionManager` is a true Singleton.
- I will add tests for the bidding logic (checking if a bid is higher than the current price).

### Manual Verification
- Verify the system builds successfully using Maven.
