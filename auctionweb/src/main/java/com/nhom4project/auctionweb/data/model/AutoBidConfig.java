package com.nhom4project.auctionweb.data.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Cấu hình Auto-Bidding cho một Bidder trên một phiên Auction.
 * Hệ thống sẽ tự động đặt giá thay người dùng khi có bid mới từ đối thủ,
 * với mức tăng (increment) và không vượt quá maxBid.
 * Ưu tiên theo thời điểm đăng ký (registeredAt).
 */
@Entity
@Table(name = "auto_bid_configs")
public class AutoBidConfig extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    @ManyToOne
    @JoinColumn(name = "bidder_id", nullable = false)
    private Bidder bidder;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal maxBid;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal increment;

    private boolean active = true;

    private LocalDateTime registeredAt = LocalDateTime.now();

    // === Getters & Setters ===

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public Bidder getBidder() {
        return bidder;
    }

    public void setBidder(Bidder bidder) {
        this.bidder = bidder;
    }

    public BigDecimal getMaxBid() {
        return maxBid;
    }

    public void setMaxBid(BigDecimal maxBid) {
        this.maxBid = maxBid;
    }

    public BigDecimal getIncrement() {
        return increment;
    }

    public void setIncrement(BigDecimal increment) {
        this.increment = increment;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }
}
