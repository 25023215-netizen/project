package com.nhom4project.auctionweb.backend.controller;

import com.nhom4project.auctionweb.backend.model.*;
import com.nhom4project.auctionweb.backend.repository.AuctionHistoryRepository;
import com.nhom4project.auctionweb.backend.repository.AuctionRepository;
import com.nhom4project.auctionweb.backend.repository.BidRepository;
import com.nhom4project.auctionweb.backend.service.AuctionService;
import com.nhom4project.auctionweb.backend.service.UserService;
import com.nhom4project.auctionweb.backend.repository.ItemRepository;
import com.nhom4project.auctionweb.backend.repository.AutoBidConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * REST Controller cho tính năng quản trị (Admin).
 * Bao gồm: quản lý user, duyệt/từ chối sản phẩm, xem thống kê.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private AuctionHistoryRepository auctionHistoryRepository;

    @Autowired
    private AutoBidConfigRepository autoBidConfigRepository;

    @Autowired
    private ItemRepository itemRepository;

    // ==================== User Management ====================

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/users/{id}/lock")
    public ResponseEntity<?> toggleLockUser(@PathVariable Long id) {
        try {
            userService.toggleLockUser(id);
            return ResponseEntity.ok("User lock status toggled!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==================== Auction Management ====================

    @GetMapping("/auctions")
    public ResponseEntity<?> getAllAuctions() {
        return ResponseEntity.ok(auctionService.listAuctions());
    }

    @PostMapping("/auctions/{id}/approve")
    public ResponseEntity<?> approveAuction(@PathVariable Long id) {
        try {
            Auction auction = auctionService.getAuctionById(id)
                    .orElseThrow(() -> new Exception("Auction not found!"));
            if (auction.getStatus() != AuctionStatus.PENDING) {
                return ResponseEntity.badRequest().body("Auction is not in PENDING status!");
            }
            auction.setStatus(AuctionStatus.OPEN);
            auctionRepository.save(auction);
            return ResponseEntity.ok("Auction approved!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/auctions/{id}/reject")
    public ResponseEntity<?> rejectAuction(@PathVariable Long id) {
        try {
            Auction auction = auctionService.getAuctionById(id)
                    .orElseThrow(() -> new Exception("Auction not found!"));
            auction.setStatus(AuctionStatus.CANCELED);
            auctionRepository.save(auction);
            return ResponseEntity.ok("Auction rejected!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==================== Statistics ====================

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        List<User> users = userService.getAllUsers();
        stats.put("totalUsers", users.size());
        stats.put("totalSellers", users.stream().filter(u -> u instanceof Seller).count());
        stats.put("totalBidders", users.stream().filter(u -> u instanceof Bidder).count());
        stats.put("totalAdmins", users.stream().filter(u -> u instanceof Admin).count());

        List<Auction> auctions = auctionService.listAuctions();
        stats.put("totalAuctions", auctions.size());
        stats.put("runningAuctions", auctions.stream().filter(a -> a.getStatus() == AuctionStatus.RUNNING).count());
        stats.put("finishedAuctions", auctions.stream().filter(a -> a.getStatus() == AuctionStatus.FINISHED).count());
        stats.put("pendingAuctions", auctions.stream().filter(a -> a.getStatus() == AuctionStatus.PENDING).count());



        // Bid cao nhất
        BigDecimal highestBid = auctions.stream()
                .map(Auction::getCurrentPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        stats.put("highestBid", highestBid);

        // Tổng số bid transactions
        stats.put("totalBidTransactions", bidRepository.count());

        return ResponseEntity.ok(stats);
    }

    // ==================== Bid History ====================

    @GetMapping("/auctions/{id}/bids")
    public ResponseEntity<?> getBidHistory(@PathVariable Long id) {
        return ResponseEntity.ok(auctionService.getBidHistory(id));
    }

    @GetMapping("/auctions/history/all")
    public ResponseEntity<?> getAllAuctionHistories() {
        return ResponseEntity.ok(auctionService.listAllAuctionHistoriesForAdmin());
    }

    @GetMapping("/clear-data")
    public ResponseEntity<?> clearDataExceptUsers() {
        try {
            auctionHistoryRepository.deleteAll();
            autoBidConfigRepository.deleteAll();
            bidRepository.deleteAll();
            auctionRepository.deleteAll();
            itemRepository.deleteAll();
            com.nhom4project.auctionweb.backend.service.AuctionManager.getInstance().getActiveAuctions().clear();
            return ResponseEntity.ok("Cleared all data except users!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error clearing data: " + e.getMessage());
        }
    }

    @DeleteMapping("/auctions/history/{id}")
    public ResponseEntity<?> deleteAuctionHistory(@PathVariable Long id) {
        try {
            if (id < 0) {
                return ResponseEntity.badRequest().body("Lỗi: Phiên đấu giá này vẫn đang tồn tại trong hệ thống. Hãy xóa nó ở tab Quản lý Đấu giá trước khi xóa lịch sử!");
            }
            if (!auctionHistoryRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            auctionHistoryRepository.deleteById(id);
            return ResponseEntity.ok("Auction history deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
