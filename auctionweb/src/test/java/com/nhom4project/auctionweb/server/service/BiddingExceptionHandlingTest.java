package com.nhom4project.auctionweb.server.service;

import com.nhom4project.auctionweb.server.model.*;
import com.nhom4project.auctionweb.server.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TEST LOGIC - MỤC 3.1.5: XỬ LÝ LỖI & NGOẠI LỆ - Nhóm 4
 */
@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BiddingExceptionHandlingTest {

    @Autowired private AuctionService auctionService;
    @Autowired private AuctionRepository auctionRepository;
    @Autowired private UserRepository userRepository;

    private Seller seller;
    private Bidder bidder;
    private Auction runningAuction;

    @BeforeEach
    void setUp() {
        long ts = System.nanoTime();
        seller = new Seller();
        seller.setUsername("s_" + ts);
        seller.setPassword("pass12345");
        seller.setEmail("s_" + ts + "@test.com");
        seller.setRole(Roles.SELLER);
        userRepository.save(seller);

        bidder = new Bidder();
        bidder.setUsername("b_" + ts);
        bidder.setPassword("pass12345");
        bidder.setEmail("b_" + ts + "@test.com");
        bidder.setRole(Roles.BIDDER);
        userRepository.save(bidder);

        runningAuction = new Auction();
        runningAuction.setTitle("Test Auction");
        runningAuction.setStatus(AuctionStatus.RUNNING);
        runningAuction.setStartingPrice(new BigDecimal("1000000"));
        runningAuction.setCurrentPrice(new BigDecimal("1000000"));
        runningAuction.setEndTime(LocalDateTime.now().plusHours(1));
        runningAuction.setSeller(seller);
        auctionRepository.save(runningAuction);
    }

    // ============================================================
    // KIỂM TRA CÁC LỖI LOGIC ĐẤU GIÁ (MỤC 3.1.5)
    // ============================================================

    @Test
    @DisplayName("Lỗi: Đặt giá thấp hơn giá hiện tại")
    void testBidLowerThanCurrent_Failure() {
        assertThrows(IllegalArgumentException.class, () ->
            auctionService.placeBid(runningAuction.getId(), bidder.getId(), new BigDecimal("500000"))
        );
    }

    @Test
    @DisplayName("Lỗi: Đấu giá khi phiên đã kết thúc")
    void testBidOnFinishedAuction_Failure() {
        runningAuction.setStatus(AuctionStatus.FINISHED);
        auctionRepository.save(runningAuction);

        assertThrows(IllegalStateException.class, () ->
            auctionService.placeBid(runningAuction.getId(), bidder.getId(), new BigDecimal("2000000"))
        );
    }

    @Test
    @DisplayName("Lỗi: Kết nối/Dữ liệu không hợp lệ (ID sai)")
    void testBidOnInvalidAuctionId_Failure() {
        assertThrows(IllegalArgumentException.class, () ->
            auctionService.placeBid(999999L, bidder.getId(), new BigDecimal("2000000"))
        );
    }
}
