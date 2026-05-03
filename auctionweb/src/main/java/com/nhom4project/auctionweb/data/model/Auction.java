package com.nhom4project.auctionweb.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "auction_sessions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Auction extends BaseEntity {
    @Version
    private Long version;

    @Column(precision = 15, scale = 2)
    private BigDecimal startingPrice;
    @Column(length = 200)
    private String title;

    @Column(length = 60)
    private String category;

    @Column(length = 1000)
    private String description;

    @Column(precision = 15, scale = 2)
    private BigDecimal currentPrice;

    private Integer bidCount = 0;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private AuctionStatus status = AuctionStatus.OPEN;

    @OneToOne
    @JoinColumn(name = "item_id")
    @JsonIgnoreProperties({"seller"})
    private Item item;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    @JsonIgnoreProperties({"password", "email"})
    private Bidder winner;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    @JsonIgnoreProperties({"password", "email"})
    private Seller seller;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public BigDecimal getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(BigDecimal startingPrice) {
        this.startingPrice = startingPrice;
    }

    public String getTitle() {
        if (title != null) {
            return title;
        }
        return item != null ? item.getName() : "";
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
        if (description != null) {
            return description;
        }
        return item != null ? item.getDescription() : "";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getCurrentPrice() {
        if (currentPrice != null) {
            return currentPrice;
        }
        if (item != null && item.getCurrentPrice() != null) {
            return BigDecimal.valueOf(item.getCurrentPrice());
        }
        return BigDecimal.ZERO;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Integer getBidCount() {
        return bidCount == null ? 0 : bidCount;
    }

    public void setBidCount(Integer bidCount) {
        this.bidCount = bidCount;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public AuctionStatus getStatus() {
        return status;
    }

    public void setStatus(AuctionStatus status) {
        this.status = status;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Bidder getWinner() {
        return winner;
    }

    public void setWinner(Bidder winner) {
        this.winner = winner;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }
}
