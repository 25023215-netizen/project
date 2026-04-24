package com.nhom4project.auctionweb.data.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "bid_transactions")
public class BidTransaction extends BaseEntity {
    private Double amount;
    private LocalDateTime bidTime;

    @ManyToOne
    @JoinColumn(name = "bidder_id")
    private Bidder bidder;

    @ManyToOne
    @JoinColumn(name = "auction_id")
    private Auction auction;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getBidTime() {
        return bidTime;
    }

    public void setBidTime(LocalDateTime bidTime) {
        this.bidTime = bidTime;
    }

    public Bidder getBidder() {
        return bidder;
    }

    public void setBidder(Bidder bidder) {
        this.bidder = bidder;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }
}
