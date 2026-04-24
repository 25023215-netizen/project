package com.nhom4project.auctionweb.data.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Entity
@Table(name = "BidTransactions")
@Data
@EqualsAndHashCode(callSuper = true)
public class BidTransaction extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "auction_id")
    private Auction auction;

    @ManyToOne
    @JoinColumn(name = "bidder_id")
    private Bidder bidder;

    private Double bidAmount;
    private LocalDateTime bidTime;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
        this.bidTime = LocalDateTime.now();
    }
}
