package com.nhom4project.auctionweb.server.service;

import com.nhom4project.auctionweb.server.model.*;
import com.nhom4project.auctionweb.server.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests cho AuctionService - Nhóm 4.
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuctionServiceTest {

    @Autowired private AuctionService auctionService;
    @Autowired private AuctionRepository auctionRepository;
    @Autowired private UserRepository userRepository;

    private Seller seller;
    private Bidder bidder;
    private Auction testAuction;

    @BeforeEach
    void setUp() {
        long ts = System.nanoTime();
        seller = new Seller();
        seller.setUsername("s_" + ts);
        seller.setPassword("password12345");
        seller.setFullname("Test Seller");
        seller.setEmail("s_" + ts + "@test.com");
        seller.setRole(Roles.SELLER);
        userRepository.save(seller);

        bidder = new Bidder();
        bidder.setUsername("b_" + ts);
        bidder.setPassword("password12345");
        bidder.setFullname("Test Bidder");
        bidder.setEmail("b_" + ts + "@test.com");
        bidder.setRole(Roles.BIDDER);
        userRepository.save(bidder);

        testAuction = new Auction();
        testAuction.setTitle("Test Auction");
        testAuction.setStatus(AuctionStatus.RUNNING);
        testAuction.setStartingPrice(new BigDecimal("1000000"));
        testAuction.setCurrentPrice(new BigDecimal("1000000"));
        testAuction.setEndTime(LocalDateTime.now().plusHours(1));
        testAuction.setSeller(seller);
        auctionRepository.save(testAuction);
    }

    @Test
    @DisplayName("Đặt giá thành công khi giá cao hơn hiện tại")
    void testPlaceBid_Success() {
        boolean result = auctionService.placeBid(testAuction.getId(), bidder.getId(), new BigDecimal("1500000"));
        assertTrue(result);
        
        Auction updated = auctionRepository.findById(testAuction.getId()).orElseThrow();
        assertEquals(0, new BigDecimal("1500000").compareTo(updated.getCurrentPrice()));
    }
}
