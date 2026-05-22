package com.nhom4project.auctionweb.backend.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test Suite cho các Entities (Domain Models) của AuctionWeb.
 * Đảm bảo tính nhất quán của dữ liệu, thừa kế và logic nghiệp vụ cốt lõi.
 */
public class ModelEntitiesTest {

    @Test
    @DisplayName("Kiểm tra thực thể Seller - Thừa kế từ User")
    public void testSellerEntity() {
        Seller seller = new Seller();
        seller.setId(10L);
        seller.setUsername("seller_model");
        seller.setPassword("password123");
        seller.setEmail("seller@model.com");
        seller.setFullname("Model Seller");
        seller.setRole(Roles.SELLER);
        seller.setStoreName("Tech Store");
        seller.setStoreRating(4.8);

        assertEquals(10L, seller.getId());
        assertEquals("seller_model", seller.getUsername());
        assertEquals("password123", seller.getPassword());
        assertEquals("seller@model.com", seller.getEmail());
        assertEquals("Model Seller", seller.getFullname());
        assertEquals(Roles.SELLER, seller.getRole());
        assertEquals("Tech Store", seller.getStoreName());
        assertEquals(4.8, seller.getStoreRating());
        assertFalse(seller.isLocked());
    }

    @Test
    @DisplayName("Kiểm tra thực thể Bidder - Thừa kế từ User")
    public void testBidderEntity() {
        Bidder bidder = new Bidder();
        bidder.setId(11L);
        bidder.setUsername("bidder_model");
        bidder.setPassword("password123");
        bidder.setEmail("bidder@model.com");
        bidder.setFullname("Model Bidder");
        bidder.setRole(Roles.BIDDER);
        bidder.setLocked(true);
        bidder.setRating(4.5);

        assertEquals(11L, bidder.getId());
        assertEquals("bidder_model", bidder.getUsername());
        assertEquals("password123", bidder.getPassword());
        assertEquals("bidder@model.com", bidder.getEmail());
        assertEquals("Model Bidder", bidder.getFullname());
        assertEquals(Roles.BIDDER, bidder.getRole());
        assertTrue(bidder.isLocked());
        assertEquals(4.5, bidder.getRating());
    }

    @Test
    @DisplayName("Kiểm tra thực thể Admin - Thừa kế từ User")
    public void testAdminEntity() {
        Admin admin = new Admin();
        admin.setId(12L);
        admin.setUsername("admin_model");
        admin.setPassword("password123");
        admin.setEmail("admin@model.com");
        admin.setFullname("Model Admin");
        admin.setRole(Roles.ADMIN);
        admin.setAdminLevel("SUPER");

        assertEquals(12L, admin.getId());
        assertEquals("admin_model", admin.getUsername());
        assertEquals("password123", admin.getPassword());
        assertEquals("admin@model.com", admin.getEmail());
        assertEquals("Model Admin", admin.getFullname());
        assertEquals(Roles.ADMIN, admin.getRole());
        assertEquals("SUPER", admin.getAdminLevel());
    }

    @Test
    @DisplayName("Kiểm tra thực thể Electronics - Thừa kế từ Item")
    public void testElectronicsEntity() {
        Seller seller = new Seller();
        seller.setId(1L);

        Electronics phone = new Electronics();
        phone.setId(20L);
        phone.setName("iPhone 15");
        phone.setDescription("Standard Edition");
        phone.setStartingPrice(1000.0);
        phone.setCurrentPrice(1200.0);
        phone.setSeller(seller);
        phone.setBrand("Apple");
        phone.setModelName("15");

        assertEquals(20L, phone.getId());
        assertEquals("iPhone 15", phone.getName());
        assertEquals("Standard Edition", phone.getDescription());
        assertEquals(1000.0, phone.getStartingPrice());
        assertEquals(1200.0, phone.getCurrentPrice());
        assertEquals(seller, phone.getSeller());
        assertEquals("Apple", phone.getBrand());
        assertEquals("15", phone.getModelName());
    }

    @Test
    @DisplayName("Kiểm tra thực thể Vehicle - Thừa kế từ Item")
    public void testVehicleEntity() {
        Vehicle car = new Vehicle();
        car.setId(21L);
        car.setName("Tesla Model 3");
        car.setStartingPrice(40000.0);
        car.setManufacturer("Tesla");
        car.setReleaseYear(2024);

        assertEquals(21L, car.getId());
        assertEquals("Tesla Model 3", car.getName());
        assertEquals(40000.0, car.getStartingPrice());
        assertEquals("Tesla", car.getManufacturer());
        assertEquals(2024, car.getReleaseYear());
    }

    @Test
    @DisplayName("Kiểm tra thực thể Art - Thừa kế từ Item")
    public void testArtEntity() {
        Art art = new Art();
        art.setId(22L);
        art.setName("Mona Lisa Replica");
        art.setArtist("Da Vinci");
        art.setMedium("Oil Painting");

        assertEquals(22L, art.getId());
        assertEquals("Mona Lisa Replica", art.getName());
        assertEquals("Da Vinci", art.getArtist());
        assertEquals("Oil Painting", art.getMedium());
    }

    @Test
    @DisplayName("Kiểm tra thực thể Auction và Trạng thái Đấu giá")
    public void testAuctionEntity() {
        Seller seller = new Seller();
        seller.setId(1L);

        Electronics laptop = new Electronics();
        laptop.setId(5L);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = now.plusHours(2);

        Auction auction = new Auction();
        auction.setId(30L);
        auction.setTitle("Premium Laptop");
        auction.setCategory("Electronics");
        auction.setDescription("ROG Laptop");
        auction.setStartingPrice(new BigDecimal("1500"));
        auction.setCurrentPrice(new BigDecimal("1700"));
        auction.setBidCount(5);
        auction.setStartTime(now);
        auction.setEndTime(end);
        auction.setStatus(AuctionStatus.RUNNING);
        auction.setSeller(seller);
        auction.setItem(laptop);

        assertEquals(30L, auction.getId());
        assertEquals("Premium Laptop", auction.getTitle());
        assertEquals("Electronics", auction.getCategory());
        assertEquals("ROG Laptop", auction.getDescription());
        assertEquals(new BigDecimal("1500"), auction.getStartingPrice());
        assertEquals(new BigDecimal("1700"), auction.getCurrentPrice());
        assertEquals(5, auction.getBidCount());
        assertEquals(now, auction.getStartTime());
        assertEquals(end, auction.getEndTime());
        assertEquals(AuctionStatus.RUNNING, auction.getStatus());
        assertEquals(seller, auction.getSeller());
        assertEquals(laptop, auction.getItem());
    }

    @Test
    @DisplayName("Kiểm tra thực thể AutoBidConfig")
    public void testAutoBidConfigEntity() {
        Bidder bidder = new Bidder();
        bidder.setId(3L);

        Auction auction = new Auction();
        auction.setId(4L);

        LocalDateTime now = LocalDateTime.now();

        AutoBidConfig config = new AutoBidConfig();
        config.setId(40L);
        config.setBidder(bidder);
        config.setAuction(auction);
        config.setMaxBid(new BigDecimal("1000"));
        config.setIncrement(new BigDecimal("50"));
        config.setActive(false);
        config.setRegisteredAt(now);

        assertEquals(40L, config.getId());
        assertEquals(bidder, config.getBidder());
        assertEquals(auction, config.getAuction());
        assertEquals(new BigDecimal("1000"), config.getMaxBid());
        assertEquals(new BigDecimal("50"), config.getIncrement());
        assertFalse(config.isActive());
        assertEquals(now, config.getRegisteredAt());
    }

    @Test
    @DisplayName("Kiểm tra thực thể BidTransaction")
    public void testBidTransactionEntity() {
        Bidder bidder = new Bidder();
        bidder.setId(2L);

        Auction auction = new Auction();
        auction.setId(8L);

        LocalDateTime now = LocalDateTime.now();

        BidTransaction tx = new BidTransaction();
        tx.setId(50L);
        tx.setBidder(bidder);
        tx.setAuction(auction);
        tx.setAmount(150.0);
        tx.setBidTime(now);

        assertEquals(50L, tx.getId());
        assertEquals(bidder, tx.getBidder());
        assertEquals(auction, tx.getAuction());
        assertEquals(150.0, tx.getAmount());
        assertEquals(now, tx.getBidTime());
    }

    @Test
    @DisplayName("Kiểm tra các nhánh biên và giá trị mặc định của Auction")
    public void testAuctionEdgeCases() {
        Auction auction = new Auction();

        // 1. Version getter/setter
        auction.setVersion(99L);
        assertEquals(99L, auction.getVersion());

        // 2. Title edge cases
        auction.setTitle(null);
        auction.setItem(null);
        assertEquals("", auction.getTitle());

        Item item = new Item() {};
        item.setName("Test Item");
        auction.setItem(item);
        assertEquals("Test Item", auction.getTitle());

        // 3. Description edge cases
        auction.setDescription(null);
        auction.setItem(null);
        assertEquals("", auction.getDescription());

        item.setDescription("Test Desc");
        auction.setItem(item);
        assertEquals("Test Desc", auction.getDescription());

        // 4. Current price edge cases
        auction.setCurrentPrice(null);
        auction.setItem(null);
        assertEquals(BigDecimal.ZERO, auction.getCurrentPrice());

        item.setCurrentPrice(null);
        auction.setItem(item);
        assertEquals(BigDecimal.ZERO, auction.getCurrentPrice());

        item.setCurrentPrice(450.50);
        auction.setItem(item);
        assertEquals(BigDecimal.valueOf(450.50), auction.getCurrentPrice());

        // 5. BidCount edge cases
        auction.setBidCount(null);
        assertEquals(0, auction.getBidCount());
    }
}
