package com.nhom4project.auctionweb.server.service;

import com.nhom4project.auctionweb.server.model.*;
import com.nhom4project.auctionweb.server.repository.*;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Service xử lý toàn bộ logic đấu giá.
 * - Concurrent Bidding: Optimistic Locking (@Version) + retry
 * - Anti-sniping: gia hạn thêm 60s nếu bid trong 30s cuối
 * - Auto-bidding: tự động đặt giá thay người dùng
 * - Observer Pattern: broadcast qua WebSocket khi có thay đổi
 */
@Service
@Transactional
public class AuctionService {

    private static final Logger log = LoggerFactory.getLogger(AuctionService.class);

    /** Anti-sniping: nếu bid trong X giây cuối -> gia hạn thêm Y giây */
    private static final long ANTI_SNIPE_THRESHOLD_SECONDS = 30;
    private static final long ANTI_SNIPE_EXTENSION_SECONDS = 60;

    /** Số lần retry khi gặp optimistic locking failure */
    private static final int MAX_RETRIES = 3;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AutoBidConfigRepository autoBidConfigRepository;

    @Autowired
    private AuctionHistoryRepository auctionHistoryRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    @Lazy
    private AuctionService self;

    private final ConcurrentHashMap<Long, ReentrantLock> auctionLocks = new ConcurrentHashMap<>();

    private ReentrantLock getLockForAuction(Long auctionId) {
        return auctionLocks.computeIfAbsent(auctionId, id -> new ReentrantLock());
    }

    @PostConstruct
    public void seedAuctions() {
        if (auctionRepository.count() > 0) {
            return;
        }

        auctionRepository.save(createAuction(
                "iPhone 15 Pro Max 256GB", "Electronics",
                "May moi full box, mau Titan Den.",
                new BigDecimal("20000000"), new BigDecimal("25000000"),
                18, AuctionStatus.RUNNING, LocalDateTime.now().plusDays(2)
        ));
        auctionRepository.save(createAuction(
                "Tranh Son Dau - Ho Guom", "Art",
                "Tranh ve tay kich thuoc 80x60cm.",
                new BigDecimal("3000000"), new BigDecimal("5200000"),
                9, AuctionStatus.RUNNING, LocalDateTime.now().plusDays(3)
        ));
        auctionRepository.save(createAuction(
                "Honda Wave Alpha 2023", "Vehicle",
                "Xe con moi 95%, bao duong tot.",
                new BigDecimal("12000000"), new BigDecimal("15000000"),
                4, AuctionStatus.OPEN, LocalDateTime.now().plusDays(4)
        ));
    }

    // ==================== QUERIES ====================

    public List<Auction> listAuctions() {
        return auctionRepository.findAll();
    }

    public Optional<Auction> getAuctionById(Long id) {
        return auctionRepository.findById(id);
    }

    public List<Auction> listAuctionsBySeller(Long sellerId) {
        return auctionRepository.findBySellerId(sellerId);
    }

    public List<BidTransaction> getBidHistory(Long auctionId) {
        return bidRepository.findByAuctionIdOrderByBidTimeDesc(auctionId);
    }

    // ==================== AUCTION LIFECYCLE ====================

    /**
     * Tạo mới một phiên đấu giá.
     * Giới hạn: mỗi phiên đấu giá chỉ kéo dài tối đa 1 giờ.
     */
    public Auction createAuction(String title, String category, String description,
                                  BigDecimal startingPrice, Long sellerId,
                                  LocalDateTime startTime, LocalDateTime endTime) {
        User user = userRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));
        if (!(user instanceof Seller seller)) {
            throw new IllegalArgumentException("User is not a Seller");
        }

        LocalDateTime start = startTime != null ? startTime : LocalDateTime.now();
        // Sử dụng endTime nếu được truyền vào, nếu không mặc định +24 giờ
        LocalDateTime end = endTime != null ? endTime : start.plusHours(24);

        Auction auction = new Auction();
        auction.setTitle(title);
        auction.setCategory(category);
        auction.setDescription(description);
        auction.setStartingPrice(startingPrice);
        auction.setCurrentPrice(startingPrice);
        auction.setBidCount(0);
        auction.setSeller(seller);
        auction.setStartTime(start);
        auction.setEndTime(end);
        auction.setStatus(AuctionStatus.OPEN);

        Auction saved = auctionRepository.save(auction);
        self.broadcastAuctionList();
        return saved;
    }

    public void startAuction(Long id) {
        Auction auction = findAuction(id);
        if (auction.getStatus() != AuctionStatus.OPEN) {
            throw new IllegalStateException("Chi co the bat dau phien dau gia o trang thai OPEN");
        }
        auction.setStatus(AuctionStatus.RUNNING);
        auction.setStartTime(LocalDateTime.now());
        auctionRepository.save(auction);
        AuctionManager.getInstance().registerAuction(auction);
        self.broadcastAuctionUpdate(buildAuctionPayload(auction));
    }

    /**
     * Kết thúc phiên đấu giá thủ công.
     */
    public void endAuction(Long id, Long userId, Roles role) {
        Auction auction = findAuction(id);
        
        // Permission check: Admin can end any, Seller can end their own
        if (role != Roles.ADMIN) {
            if (auction.getSeller() == null || !auction.getSeller().getId().equals(userId)) {
                throw new IllegalStateException("Ban khong co quyen ket thuc phien dau gia nay");
            }
        }

        auction.setStatus(AuctionStatus.FINISHED);
        auctionRepository.save(auction);
        AuctionManager.getInstance().updateStatus(id, AuctionStatus.FINISHED);
        self.broadcastAuctionUpdate(buildAuctionPayload(auction));
        self.broadcastAuctionList();
    }

    /**
     * Xóa phiên đấu giá.
     */
    public void deleteAuction(Long id, Long userId, Roles role) {
        Auction auction = findAuction(id);

        // Permission check: Admin can delete any, Seller can delete their own
        if (role != Roles.ADMIN) {
            if (auction.getSeller() == null || !auction.getSeller().getId().equals(userId)) {
                throw new IllegalStateException("Ban khong co quyen xoa phien dau gia nay");
            }
            
            // Check if auction is expired (either finished or endTime has passed)
            boolean isExpired = AuctionStatus.FINISHED.equals(auction.getStatus())
                    || (auction.getEndTime() != null && LocalDateTime.now().isAfter(auction.getEndTime()));
                    
            // Seller can delete expired auctions even if they have bids.
            // But they cannot delete active RUNNING auctions that already have bids.
            boolean isRunningAndHasBids = AuctionStatus.RUNNING.equals(auction.getStatus())
                    && !isExpired
                    && auction.getBidCount() > 0;
                    
            if (isRunningAndHasBids) {
                throw new IllegalStateException("Khong the xoa phien dau gia dang dien ra va co nguoi dat gia");
            }
        }

        // Save to history before deleting
        AuctionHistory history = new AuctionHistory();
        history.setAuctionId(auction.getId());
        history.setTitle(auction.getTitle());
        history.setCategory(auction.getCategory());
        history.setDescription(auction.getDescription());
        history.setStartingPrice(auction.getStartingPrice());
        history.setWinningPrice(auction.getCurrentPrice());
        if (auction.getWinner() != null) {
            history.setWinnerId(auction.getWinner().getId());
            history.setWinnerName(auction.getWinner().getUsername());
        }
        if (auction.getSeller() != null) {
            history.setSellerId(auction.getSeller().getId());
            history.setSellerName(auction.getSeller().getUsername());
        }
        history.setEndTime(auction.getEndTime());
        history.setDeletedAt(LocalDateTime.now());
        auctionHistoryRepository.save(history);

        // Delete associated data
        bidRepository.deleteByAuctionId(id);
        autoBidConfigRepository.deleteByAuctionId(id);
        
        auctionRepository.delete(auction);
        self.broadcastAuctionList();
    }

    // ==================== BIDDING (concurrent-safe) ====================

    /**
     * Đặt giá thầu - xử lý an toàn với Optimistic Locking + retry.
     * Sử dụng Fine-grained Striped Lock theo ID phiên đấu giá thay vì khóa synchronized toàn cục.
     * Tác vụ chạy ngầm được chuyển giao bất đồng bộ để tránh đệ quy đồng bộ nghẽn luồng.
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public boolean placeBid(Long auctionId, Long bidderId, BigDecimal amount) {
        ReentrantLock lock = getLockForAuction(auctionId);
        lock.lock();
        try {
            for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
                try {
                    return self.doPlaceBid(auctionId, bidderId, amount);
                } catch (ObjectOptimisticLockingFailureException e) {
                    log.warn("Optimistic lock conflict on auction {} (attempt {}), retrying...", auctionId, attempt + 1);
                    if (attempt == MAX_RETRIES - 1) {
                        throw new IllegalStateException("Hệ thống đang bận, vui lòng thử lại sau");
                    }
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Thực hiện đặt giá thầu trong Transaction riêng để retry Optimistic Locking hoạt động đúng đắn.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean doPlaceBid(Long auctionId, Long bidderId, BigDecimal amount) {
        Auction auction = findAuction(auctionId);

        // Kiểm tra trạng thái phiên
        if (auction.getStatus() != AuctionStatus.RUNNING) {
            throw new IllegalStateException("Phien dau gia khong dang chay");
        }

        // Kiểm tra thời gian
        if (auction.getEndTime() != null && LocalDateTime.now().isAfter(auction.getEndTime())) {
            throw new IllegalStateException("Phien dau gia da het thoi gian");
        }

        // Kiểm tra giá
        if (amount.compareTo(auction.getCurrentPrice()) <= 0) {
            throw new IllegalArgumentException("Gia dat phai cao hon gia hien tai: " + auction.getCurrentPrice());
        }

        // Lấy thông tin bidder
        User user = userRepository.findById(bidderId)
                .orElseThrow(() -> new IllegalArgumentException("Bidder not found"));
        if (!(user instanceof Bidder bidder)) {
            throw new IllegalArgumentException("User is not a Bidder");
        }

        // Không cho phép bidder hiện tại đặt giá lại khi vẫn là người dẫn đầu
        if (auction.getWinner() != null && auction.getWinner().getId().equals(bidder.getId())) {
            throw new IllegalStateException("Bạn đã là người giữ giá hiện tại");
        }

        // Cập nhật auction
        auction.setCurrentPrice(amount);
        auction.setBidCount(auction.getBidCount() + 1);
        auction.setWinner(bidder);

        // Anti-sniping: gia hạn nếu bid trong X giây cuối
        applyAntiSniping(auction);

        auctionRepository.save(auction);

        // Lưu lịch sử giao dịch (BidTransaction)
        saveBidTransaction(auction, bidder, amount);

        // Đăng ký vào AuctionManager
        AuctionManager.getInstance().registerAuction(auction);

        // Observer Pattern: broadcast qua WebSocket bất đồng bộ (Async)
        self.broadcastAuctionUpdate(buildAuctionPayload(auction));

        // Kích hoạt Auto-bidding bất đồng bộ (Async) cho các bidder khác
        self.processAutoBids(auctionId, bidderId);

        return true;
    }

    // ==================== ANTI-SNIPING ====================

    /**
     * Anti-sniping Algorithm:
     * Nếu bid mới trong ANTI_SNIPE_THRESHOLD_SECONDS giây cuối,
     * tự động gia hạn thêm ANTI_SNIPE_EXTENSION_SECONDS giây.
     */
    private void applyAntiSniping(Auction auction) {
        if (auction.getEndTime() == null) return;

        long secondsLeft = ChronoUnit.SECONDS.between(LocalDateTime.now(), auction.getEndTime());
        if (secondsLeft > 0 && secondsLeft <= ANTI_SNIPE_THRESHOLD_SECONDS) {
            LocalDateTime newEndTime = auction.getEndTime().plusSeconds(ANTI_SNIPE_EXTENSION_SECONDS);
            auction.setEndTime(newEndTime);
            log.info("Anti-sniping: Auction {} extended to {}", auction.getId(), newEndTime);
        }
    }

    // ==================== AUTO-BIDDING ====================

    /**
     * Đăng ký Auto-bid cho một bidder trên một auction.
     */
    @Transactional
    public AutoBidConfig registerAutoBid(Long auctionId, Long bidderId, BigDecimal maxBid, BigDecimal increment) {
        Auction auction = findAuction(auctionId);
        if (auction.getStatus() != AuctionStatus.RUNNING) {
            throw new IllegalStateException("Phien dau gia khong dang chay");
        }

        User user = userRepository.findById(bidderId)
                .orElseThrow(() -> new IllegalArgumentException("Bidder not found"));
        if (!(user instanceof Bidder bidder)) {
            throw new IllegalArgumentException("User is not a Bidder");
        }

        if (maxBid.compareTo(auction.getCurrentPrice()) <= 0) {
            throw new IllegalArgumentException("Max bid phai cao hon gia hien tai");
        }

        // Cập nhật hoặc tạo mới config
        AutoBidConfig config = autoBidConfigRepository.findByAuctionIdAndBidderId(auctionId, bidderId)
                .orElse(new AutoBidConfig());

        config.setAuction(auction);
        config.setBidder(bidder);
        config.setMaxBid(maxBid);
        config.setIncrement(increment);
        config.setActive(true);
        config.setRegisteredAt(LocalDateTime.now());

        AutoBidConfig saved = autoBidConfigRepository.save(config);

        // Kích hoạt auto-bid ngay nếu giá hiện tại thấp hơn maxBid
        self.processAutoBids(auctionId, null);

        return saved;
    }

    /**
     * Dừng Auto-bid cho một bidder trên một auction.
     */
    @Transactional
    public void stopAutoBid(Long auctionId, Long bidderId) {
        autoBidConfigRepository.findByAuctionIdAndBidderId(auctionId, bidderId).ifPresent(config -> {
            config.setActive(false);
            autoBidConfigRepository.save(config);
            log.info("Auto-bid: Bidder {} stopped auto-bid for auction {}", bidderId, auctionId);
        });
    }

    /**
     * Kiểm tra cấu hình Auto-bid của một bidder trên một auction.
     */
    public Optional<AutoBidConfig> getAutoBidConfig(Long auctionId, Long bidderId) {
        return autoBidConfigRepository.findByAuctionIdAndBidderId(auctionId, bidderId);
    }

    /**
     * Xử lý auto-bid (Proxy Bidding) cho tất cả config đang active.
     * Chạy bất đồng bộ ngầm dưới background thread pool.
     */
    @Async("taskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processAutoBids(Long auctionId, Long excludeBidderId) {
        List<AutoBidConfig> configs = autoBidConfigRepository
                .findByAuctionIdAndActiveTrueOrderByRegisteredAtAsc(auctionId);

        if (configs.isEmpty()) return;

        Auction auction = findAuction(auctionId);
        if (auction.getStatus() != AuctionStatus.RUNNING) return;

        for (AutoBidConfig config : configs) {
            // Bỏ qua bidder vừa đặt giá (tránh bid chồng lên chính mình)
            if (config.getBidder().getId().equals(excludeBidderId)) continue;

            // Bỏ qua nếu bidder đã đang dẫn đầu
            if (auction.getWinner() != null && auction.getWinner().getId().equals(config.getBidder().getId())) continue;

            // Tính giá bid tối thiểu cần thiết để dẫn đầu
            BigDecimal newBid = auction.getCurrentPrice().add(config.getIncrement());

            // Nếu newBid vượt maxBid nhưng maxBid vẫn > currentPrice -> bid đúng maxBid
            if (newBid.compareTo(config.getMaxBid()) > 0) {
                if (config.getMaxBid().compareTo(auction.getCurrentPrice()) > 0) {
                    newBid = config.getMaxBid();
                } else {
                    // maxBid đã bị vượt qua -> deactivate
                    config.setActive(false);
                    autoBidConfigRepository.save(config);
                    log.info("Auto-bid: Deactivated config for bidder {} (maxBid {} reached, current price {})",
                            config.getBidder().getId(), config.getMaxBid(), auction.getCurrentPrice());
                    continue;
                }
            }

            // Đặt giá tự động thông qua self.placeBid để được ReentrantLock bảo vệ và retry nếu có conflict
            try {
                self.placeBid(auctionId, config.getBidder().getId(), newBid);
                log.info("Auto-bid: Bidder {} placed {} on auction {} (max: {})",
                        config.getBidder().getId(), newBid, auctionId, config.getMaxBid());
                return; // Chỉ xử lý 1 auto-bid mỗi lần, trigger sẽ cascade bất đồng bộ
            } catch (Exception e) {
                log.warn("Auto-bid failed for bidder {}: {}", config.getBidder().getId(), e.getMessage());
            }
        }
    }

    // ==================== HELPERS ====================

    private void saveBidTransaction(Auction auction, Bidder bidder, BigDecimal amount) {
        BidTransaction tx = new BidTransaction();
        tx.setAuction(auction);
        tx.setBidder(bidder);
        tx.setAmount(amount.doubleValue());
        tx.setBidTime(LocalDateTime.now());
        bidRepository.save(tx);
    }

    /**
     * Tạo Map payload cho WebSocket để tránh lỗi LazyInitializationException trong luồng bất đồng bộ.
     */
    private Map<String, Object> buildAuctionPayload(Auction auction) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("auctionId", auction.getId());
        payload.put("currentPrice", auction.getCurrentPrice());
        payload.put("bidCount", auction.getBidCount());
        payload.put("status", auction.getStatus().name());
        payload.put("endTime", auction.getEndTime() != null ? auction.getEndTime().toString() : null);
        payload.put("winnerId", auction.getWinner() != null ? auction.getWinner().getId() : null);
        payload.put("winnerName", auction.getWinner() != null ? auction.getWinner().getUsername() : null);
        return payload;
    }

    /**
     * Observer Pattern: broadcast thay đổi auction qua WebSocket bất đồng bộ.
     */
    @Async("taskExecutor")
    public void broadcastAuctionUpdate(Map<String, Object> payload) {
        try {
            messagingTemplate.convertAndSend("/topic/auctions/" + payload.get("auctionId"), payload);
        } catch (Exception e) {
            log.warn("Failed to broadcast auction update: {}", e.getMessage());
        }
    }

    @Async("taskExecutor")
    public void broadcastAuctionList() {
        try {
            messagingTemplate.convertAndSend("/topic/auctions", "refresh");
        } catch (Exception e) {
            log.warn("Failed to broadcast auction list refresh: {}", e.getMessage());
        }
    }

    private Auction findAuction(Long id) {
        return auctionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Auction not found: " + id));
    }

    private Auction createAuction(String title, String category, String description,
                                   BigDecimal startingPrice, BigDecimal currentPrice,
                                   int bidCount, AuctionStatus status, LocalDateTime endTime) {
        Auction auction = new Auction();
        auction.setTitle(title);
        auction.setCategory(category);
        auction.setDescription(description);
        auction.setStartingPrice(startingPrice);
        auction.setCurrentPrice(currentPrice);
        auction.setBidCount(bidCount);
        auction.setStatus(status);
        auction.setStartTime(LocalDateTime.now());
        // Enforce 1-hour limit for seed data too
        auction.setEndTime(LocalDateTime.now().plusHours(1));
        return auction;
    }
}




