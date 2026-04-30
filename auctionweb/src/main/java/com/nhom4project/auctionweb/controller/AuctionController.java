package com.nhom4project.auctionweb.controller;

import com.nhom4project.auctionweb.data.model.Auction;
import com.nhom4project.auctionweb.data.model.Bidder;
import com.nhom4project.auctionweb.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {
    @Autowired
    private AuctionService auctionService;

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

    @PostMapping("/{id}/start")
    public ResponseEntity<?> startAuction(@PathVariable Long id) {
        try {
            auctionService.startAuction(id);
            return ResponseEntity.ok("Auction started");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/bid")
    public ResponseEntity<?> placeBid(@PathVariable Long id,
                                      @RequestParam Long bidderId,
                                      @RequestParam BigDecimal amount) {
        try {
            Bidder bidder = new Bidder();
            bidder.setId(bidderId);
            boolean success = auctionService.placeBid(id, bidder, amount);
            return success
                    ? ResponseEntity.ok("Bid placed successfully")
                    : ResponseEntity.badRequest().body("Bid failed");
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
}
