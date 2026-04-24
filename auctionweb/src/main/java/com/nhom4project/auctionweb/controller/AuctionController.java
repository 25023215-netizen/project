package com.nhom4project.auctionweb.controller;

import com.nhom4project.auctionweb.data.model.Auction;
import com.nhom4project.auctionweb.data.model.Bidder;
import com.nhom4project.auctionweb.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    @Autowired
    private AuctionService auctionService;

    @GetMapping
    public List<Auction> getAllAuctions() {
        return auctionService.getAllAuctions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Auction> getAuction(@PathVariable Long id) {
        Auction auction = auctionService.getAuctionById(id);
        return auction != null ? ResponseEntity.ok(auction) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/bid")
    public ResponseEntity<?> placeBid(@PathVariable Long id, @RequestParam Double amount, @RequestBody Bidder bidder) {
        try {
            auctionService.placeBid(id, bidder, amount);
            return ResponseEntity.ok("Bid placed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
