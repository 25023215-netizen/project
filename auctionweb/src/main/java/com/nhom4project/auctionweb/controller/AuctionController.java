package com.nhom4project.auctionweb.controller;

import com.nhom4project.auctionweb.data.model.Auction;
import com.nhom4project.auctionweb.data.model.AutoBidConfig;
import com.nhom4project.auctionweb.data.model.BidTransaction;
import com.nhom4project.auctionweb.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST Controller cho toàn bộ chức năng đấu giá.
 */
@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    @Autowired
    private AuctionService auctionService;

    // ==================== Danh sách & Chi tiết ====================

    @GetMapping
    public List<Auction> listAuctions() {
        return auctionService.listAuctions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Auction> getAuction(@PathVariable Long id) {
        return auctionService.getAuctionById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/seller/{sellerId}")
    public List<Auction> listBySeller(@PathVariable Long sellerId) {
        return auctionService.listAuctionsBySeller(sellerId);
    }

    // ==================== Tạo phiên đấu giá ====================

    /**
     * Tạo mới phiên đấu giá.
     * Body: { "title", "category", "description", "startingPrice", "sellerId", "endTime" }
     */
    @PostMapping
    public ResponseEntity<?> createAuction(@RequestBody Map<String, Object> body) {
        try {
            String title = (String) body.get("title");
            String category = (String) body.get("category");
            String description = (String) body.getOrDefault("description", "");
            BigDecimal startingPrice = new BigDecimal(body.get("startingPrice").toString());
            Long sellerId = Long.valueOf(body.get("sellerId").toString());
            LocalDateTime endTime = body.containsKey("endTime")
                    ? LocalDateTime.parse(body.get("endTime").toString())
                    : LocalDateTime.now().plusDays(3);

            Auction auction = auctionService.createAuction(title, category, description,
                    startingPrice, sellerId, null, endTime);
            return ResponseEntity.ok(auction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==================== Lifecycle ====================

    @PostMapping("/{id}/start")
    public ResponseEntity<?> startAuction(@PathVariable Long id) {
        try {
            auctionService.startAuction(id);
            return ResponseEntity.ok("Auction started");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/end")
    public ResponseEntity<?> endAuction(@PathVariable Long id) {
        try {
            auctionService.endAuction(id);
            return ResponseEntity.ok("Auction ended");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==================== Bidding ====================

    /**
     * Đặt giá thầu.
     * Body: { "bidderId": 1, "amount": 50000 }
     */
    @PostMapping("/{id}/bid")
    public ResponseEntity<?> placeBid(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            Long bidderId = Long.valueOf(body.get("bidderId").toString());
            BigDecimal amount = new BigDecimal(body.get("amount").toString());
            boolean success = auctionService.placeBid(id, bidderId, amount);
            return success
                    ? ResponseEntity.ok("Bid placed successfully")
                    : ResponseEntity.badRequest().body("Bid failed");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==================== Bid History ====================

    @GetMapping("/{id}/bids")
    public List<BidTransaction> getBidHistory(@PathVariable Long id) {
        return auctionService.getBidHistory(id);
    }

    // ==================== Auto-Bidding ====================

    /**
     * Đăng ký Auto-bid.
     * Body: { "bidderId": 1, "maxBid": 100000, "increment": 5000 }
     */
    @PostMapping("/{id}/auto-bid")
    public ResponseEntity<?> registerAutoBid(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            Long bidderId = Long.valueOf(body.get("bidderId").toString());
            BigDecimal maxBid = new BigDecimal(body.get("maxBid").toString());
            BigDecimal increment = new BigDecimal(body.get("increment").toString());

            AutoBidConfig config = auctionService.registerAutoBid(id, bidderId, maxBid, increment);
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
