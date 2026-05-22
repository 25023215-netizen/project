package com.nhom4project.auctionweb.backend.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Isolated unit tests for the Server Entity model layer.
 * Focuses on business model calculations, inheritance properties, and boundary fallbacks.
 */
public class ServerModelTest {

    @Test
    public void testUserInheritanceAndRoles() {
        Seller seller = new Seller();
        seller.setUsername("seller_bob");
        seller.setEmail("bob@gmail.com");
        seller.setRole(Roles.SELLER);
        seller.setStoreName("Bob's Antique Shop");
        seller.setStoreRating(4.8);

        assertEquals("seller_bob", seller.getUsername());
        assertEquals(Roles.SELLER, seller.getRole());
        assertEquals("Bob's Antique Shop", seller.getStoreName());
        assertEquals(4.8, seller.getStoreRating());
        assertFalse(seller.isLocked());

        Bidder bidder = new Bidder();
        bidder.setUsername("bidder_alice");
        bidder.setRole(Roles.BIDDER);
        bidder.setLocked(true);

        assertEquals("bidder_alice", bidder.getUsername());
        assertEquals(Roles.BIDDER, bidder.getRole());
        assertTrue(bidder.isLocked());
    }

    @Test
    public void testAuctionTitleFallback() {
        Auction auction = new Auction();

        // 1. Title is explicitly set
        auction.setTitle("Premium Watch Auction");
        assertEquals("Premium Watch Auction", auction.getTitle());

        // 2. Title is null, Item has a name
        auction.setTitle(null);
        Item item = new Electronics();
        item.setName("Vintage Omega Watch");
        auction.setItem(item);
        assertEquals("Vintage Omega Watch", auction.getTitle());

        // 3. Both Title and Item are null
        auction.setItem(null);
        assertEquals("", auction.getTitle());
    }

    @Test
    public void testAuctionDescriptionFallback() {
        Auction auction = new Auction();

        // 1. Description is explicitly set
        auction.setDescription("Watch is in working condition.");
        assertEquals("Watch is in working condition.", auction.getDescription());

        // 2. Description is null, Item has a description
        auction.setDescription(null);
        Item item = new Electronics();
        item.setDescription("Vintage watch description");
        auction.setItem(item);
        assertEquals("Vintage watch description", auction.getDescription());

        // 3. Both are null
        auction.setItem(null);
        assertEquals("", auction.getDescription());
    }

    @Test
    public void testAuctionCurrentPriceFallback() {
        Auction auction = new Auction();

        // 1. CurrentPrice explicitly set
        auction.setCurrentPrice(new BigDecimal("150.00"));
        assertEquals(new BigDecimal("150.00"), auction.getCurrentPrice());

        // 2. CurrentPrice is null, Item has current price
        auction.setCurrentPrice(null);
        Item item = new Electronics();
        item.setCurrentPrice(120.50);
        auction.setItem(item);
        assertEquals(BigDecimal.valueOf(120.50), auction.getCurrentPrice());

        // 3. Both are null
        auction.setItem(null);
        assertEquals(BigDecimal.ZERO, auction.getCurrentPrice());
    }

    @Test
    public void testAutoBidConfigDefaultFields() {
        AutoBidConfig config = new AutoBidConfig();
        config.setMaxBid(new BigDecimal("500.00"));
        config.setIncrement(new BigDecimal("10.00"));

        assertTrue(config.isActive());
        assertNotNull(config.getRegisteredAt());
        assertEquals(new BigDecimal("500.00"), config.getMaxBid());
        assertEquals(new BigDecimal("10.00"), config.getIncrement());
    }
}
