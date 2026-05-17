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

    @Test
    @DisplayName("Đặt giá đồng thời từ nhiều luồng (Concurrency Stress Test)")
    void testConcurrentBidding() throws InterruptedException {
        int threadCount = 10;
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(threadCount);
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        java.util.concurrent.CountDownLatch doneLatch = new java.util.concurrent.CountDownLatch(threadCount);
        
        // Tạo 10 bidder khác nhau để đặt giá đồng thời
        java.util.List<Bidder> bidders = new java.util.ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            long ts = System.nanoTime() + i;
            Bidder b = new Bidder();
            b.setUsername("b_concurrent_" + ts);
            b.setPassword("password12345");
            b.setFullname("Concurrent Bidder " + i);
            b.setEmail("b_concurrent_" + ts + "@test.com");
            b.setRole(Roles.BIDDER);
            userRepository.save(b);
            bidders.add(b);
        }

        // Kích hoạt đặt giá đồng thời
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            final Bidder b = bidders.get(i);
            BigDecimal bidAmount = new BigDecimal("1100000").add(new BigDecimal(100000 * index));
            executor.submit(() -> {
                try {
                    latch.await(); // Đợi tất cả cùng bắt đầu
                    auctionService.placeBid(testAuction.getId(), b.getId(), bidAmount);
                } catch (Exception e) {
                    // Bỏ qua lỗi conflict do optimistic lock vì đó là kết quả mong muốn
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        latch.countDown(); // Phát súng bắt đầu
        doneLatch.await(10, java.util.concurrent.TimeUnit.SECONDS);
        executor.shutdown();

        // Kiểm tra xem ít nhất có 1 phiên đặt giá thành công và giá hiện tại lớn hơn ban đầu
        Auction updated = auctionRepository.findById(testAuction.getId()).orElseThrow();
        assertTrue(updated.getBidCount() > 0);
        assertTrue(updated.getCurrentPrice().compareTo(new BigDecimal("1000000")) > 0);
        assertNotNull(updated.getWinner());
    }
}
