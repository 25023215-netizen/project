package com.nhom4project.auctionweb.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "auction_histories")
public class AuctionHistory extends BaseEntity {

    private Long auctionId;

    @Column(length = 200)
    private String title;

    @Column(length = 60)
    private String category;

    @Column(length = 1000)
    private String description;

    @Column(precision = 15, scale = 2)
    private BigDecimal startingPrice;

    @Column(precision = 15, scale = 2)
    private BigDecimal winningPrice;

    private Long winnerId;

    @Column(length = 100)
    private String winnerName;

    private Long sellerId;

    @Column(length = 100)
    private String sellerName;

    private LocalDateTime endTime;

    private LocalDateTime deletedAt;

    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(BigDecimal startingPrice) {
        this.startingPrice = startingPrice;
    }

    public BigDecimal getWinningPrice() {
        return winningPrice;
    }

    public void setWinningPrice(BigDecimal winningPrice) {
        this.winningPrice = winningPrice;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Long winnerId) {
        this.winnerId = winnerId;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
