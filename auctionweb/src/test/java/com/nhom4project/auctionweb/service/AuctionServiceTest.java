package com.nhom4project.auctionweb.service;

import com.nhom4project.auctionweb.data.model.*;
import com.nhom4project.auctionweb.data.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests cho AuctionService.
 * Kiểm tra: Concurrent Bidding, Anti-sniping, Validation, Auto-bidding.
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AuctionServiceTest {

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private AutoBidConfigRepository autoBidConfigRepository;

    private Seller seller;
    private Bidder bidder1;
    private Bidder bidder2;
    private Auction testAuction;

    @BeforeEach
    void setUp() {
        // Tạo seller
        seller = new Seller();
        seller.setUsername("test_seller_" + System.nanoTime());
        seller.setPassword("password123");
        seller.setFullname("Test Seller");
        seller.setEmail("seller_" + System.nanoTime() + "@test.com");
        seller.setRole(Roles.SELLER);
        seller.setStoreName("Test Store");
        userRepository.save(seller);

        // Tạo bidder 1
        bidder1 = new Bidder();
        bidder1.setUsername("test_bidder1_" + System.nanoTime());
        bidder1.setPassword("password123");
        bidder1.setFullname("Test Bidder 1");
        bidder1.setEmail("bidder1_" + System.nanoTime() + "@test.com");
        bidder1.setRole(Roles.BIDDER);
        userRepository.save(bidder1);

        // Tạo bidder 2
        bidder2 = new Bidder();
        bidder2.setUsername("test_bidder2_" + System.nanoTime());
        bidder2.setPassword("password123");
        bidder2.setFullname("Test Bidder 2");
        bidder2.setEmail("bidder2_" + System.nanoTime() + "@test.com");
        bidder2.setRole(Roles.BIDDER);
        userRepository.save(bidder2);

        // Tạo auction test
        testAuction = new Auction();
        testAuction.setTitle("Test Auction");
        testAuction.setCategory("Electronics");
        testAuction.setDescription("Test Description");
        testAuction.setStartingPrice(new BigDecimal("1000000"));
        testAuction.setCurrentPrice(new BigDecimal("1000000"));
        testAuction.setBidCount(0);
        testAuction.setSeller(seller);
        testAuction.setStartTime(LocalDateTime.now());
        testAuction.setEndTime(LocalDateTime.now().plusHours(2));
        testAuction.setStatus(AuctionStatus.RUNNING);
        auctionRepository.save(testAuction);
    }

    // ==================== Basic Bidding Tests ====================

    @Test
    @DisplayName("Dat gia thanh cong khi gia cao hon gia hien tai")
    void testPlaceBidSuccess() {
        boolean result = auctionService.placeBid(testAuction.getId(), bidder1.getId(), new BigDecimal("1500000"));
        assertTrue(result);

        Auction updated = auctionRepository.findById(testAuction.getId()).orElseThrow();
        assertEquals(0, new BigDecimal("1500000").compareTo(updated.getCurrentPrice()));
        assertEquals(1, updated.getBidCount());
        assertEquals(bidder1.getId(), updated.getWinner().getId());
    }

    @Test
    @DisplayName("Dat gia that bai khi gia thap hon gia hien tai")
    void testPlaceBidTooLow() {
        assertThrows(IllegalArgumentException.class, () ->
                auctionService.placeBid(testAuction.getId(), bidder1.getId(), new BigDecimal("500000"))
        );
    }

    @Test
    @DisplayName("Dat gia that bai khi phien khong o trang thai RUNNING")
    void testPlaceBidOnClosedAuction() {
        testAuction.setStatus(AuctionStatus.FINISHED);
        auctionRepository.save(testAuction);

        assertThrows(IllegalStateException.class, () ->
                auctionService.placeBid(testAuction.getId(), bidder1.getId(), new BigDecimal("2000000"))
        );
    }

    @Test
    @DisplayName("Dat gia that bai khi phien da het thoi gian")
    void testPlaceBidExpired() {
        testAuction.setEndTime(LocalDateTime.now().minusMinutes(1));
        auctionRepository.save(testAuction);

        assertThrows(IllegalStateException.class, () ->
                auctionService.placeBid(testAuction.getId(), bidder1.getId(), new BigDecimal("2000000"))
        );
    }

    // ==================== Concurrent Bidding Tests ====================

    @Test
    @DisplayName("10 threads cung dat gia dong thoi - khong bi lost update")
    void testConcurrentBidding() throws Exception {
        int numThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < numThreads; i++) {
            final BigDecimal bidAmount = new BigDecimal(1000000 + (i + 1) * 100000);
            final Long bidderId = (i % 2 == 0) ? bidder1.getId() : bidder2.getId();

            executor.submit(() -> {
                try {
                    auctionService.placeBid(testAuction.getId(), bidderId, bidAmount);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        // Kiểm tra: ít nhất 1 bid thành công
        assertTrue(successCount.get() >= 1, "At least one bid should succeed");

        // Kiểm tra: bidCount khớp với số bid thành công
        Auction updated = auctionRepository.findById(testAuction.getId()).orElseThrow();
        assertEquals(successCount.get(), updated.getBidCount(),
                "Bid count should match successful bids (no lost update)");

        // Kiểm tra: chỉ có 1 winner
        assertNotNull(updated.getWinner(), "There should be a winner");

        System.out.println("Concurrent test: " + successCount.get() + " succeeded, "
                + failCount.get() + " failed. Final price: " + updated.getCurrentPrice());
    }

    // ==================== Anti-Sniping Tests ====================

    @Test
    @DisplayName("Anti-sniping: gia han khi bid trong 30s cuoi")
    void testAntiSniping() {
        // Đặt endTime chỉ còn 20 giây
        testAuction.setEndTime(LocalDateTime.now().plusSeconds(20));
        auctionRepository.save(testAuction);

        LocalDateTime originalEndTime = testAuction.getEndTime();

        // Đặt giá -> trigger anti-sniping
        auctionService.placeBid(testAuction.getId(), bidder1.getId(), new BigDecimal("1500000"));

        Auction updated = auctionRepository.findById(testAuction.getId()).orElseThrow();

        // endTime phải được gia hạn thêm 60s
        assertTrue(updated.getEndTime().isAfter(originalEndTime),
                "End time should be extended after bid in last 30 seconds");
    }

    @Test
    @DisplayName("Anti-sniping: khong gia han khi bid con nhieu thoi gian")
    void testNoAntiSnipingWhenTimeLeft() {
        // endTime còn 2 giờ
        testAuction.setEndTime(LocalDateTime.now().plusHours(2));
        auctionRepository.save(testAuction);

        // Reload from DB to get the stored precision
        LocalDateTime originalEndTime = auctionRepository.findById(testAuction.getId())
                .orElseThrow().getEndTime();

        auctionService.placeBid(testAuction.getId(), bidder1.getId(), new BigDecimal("1500000"));

        Auction updated = auctionRepository.findById(testAuction.getId()).orElseThrow();

        // endTime không đổi (so sánh truncated tới giây do H2 precision)
        assertEquals(
                originalEndTime.truncatedTo(java.time.temporal.ChronoUnit.SECONDS),
                updated.getEndTime().truncatedTo(java.time.temporal.ChronoUnit.SECONDS),
                "End time should NOT be extended when there is still time left");
    }

    // ==================== Bid History Tests ====================

    @Test
    @DisplayName("Lich su bid duoc luu dung sau moi lan dat gia")
    void testBidHistoryRecorded() {
        auctionService.placeBid(testAuction.getId(), bidder1.getId(), new BigDecimal("1500000"));
        auctionService.placeBid(testAuction.getId(), bidder2.getId(), new BigDecimal("2000000"));
        auctionService.placeBid(testAuction.getId(), bidder1.getId(), new BigDecimal("2500000"));

        var history = auctionService.getBidHistory(testAuction.getId());
        assertTrue(history.size() >= 3, "Should have at least 3 bid transactions");
    }

    // ==================== Auto-Bidding Tests ====================

    @Test
    @DisplayName("Auto-bid: tu dong dat gia khi co doi thu")
    void testAutoBidding() {
        // Bidder2 đăng ký auto-bid: maxBid = 3M, increment = 200K
        auctionService.registerAutoBid(testAuction.getId(), bidder2.getId(),
                new BigDecimal("3000000"), new BigDecimal("200000"));

        // Bidder1 đặt giá 1.5M -> hệ thống tự động đặt 1.7M cho bidder2
        auctionService.placeBid(testAuction.getId(), bidder1.getId(), new BigDecimal("1500000"));

        Auction updated = auctionRepository.findById(testAuction.getId()).orElseThrow();

        // Giá hiện tại phải > 1.5M (do auto-bid)
        assertTrue(updated.getCurrentPrice().compareTo(new BigDecimal("1500000")) > 0,
                "Auto-bid should have increased the price above 1.5M");

        // Winner phải là bidder2 (do auto-bid)
        assertEquals(bidder2.getId(), updated.getWinner().getId(),
                "Auto-bidder should be the current winner");
    }

    // ==================== Auction Lifecycle Tests ====================

    @Test
    @DisplayName("Tao phien dau gia moi")
    void testCreateAuction() {
        Auction newAuction = auctionService.createAuction(
                "New Test Auction", "Art", "Description",
                new BigDecimal("500000"), seller.getId(),
                null, LocalDateTime.now().plusDays(1)
        );

        assertNotNull(newAuction.getId());
        assertEquals(AuctionStatus.OPEN, newAuction.getStatus());
        assertEquals(0, new BigDecimal("500000").compareTo(newAuction.getCurrentPrice()));
    }

    @Test
    @DisplayName("Bat dau va ket thuc phien dau gia")
    void testAuctionLifecycle() {
        Auction auction = auctionService.createAuction(
                "Lifecycle Test", "Vehicle", "Test",
                new BigDecimal("100000"), seller.getId(),
                null, LocalDateTime.now().plusDays(1)
        );

        assertEquals(AuctionStatus.OPEN, auction.getStatus());

        auctionService.startAuction(auction.getId());
        Auction started = auctionRepository.findById(auction.getId()).orElseThrow();
        assertEquals(AuctionStatus.RUNNING, started.getStatus());

        auctionService.endAuction(auction.getId());
        Auction ended = auctionRepository.findById(auction.getId()).orElseThrow();
        assertEquals(AuctionStatus.FINISHED, ended.getStatus());
    }
}
