package com.nhom4project.auctionweb.service;

import com.nhom4project.auctionweb.data.model.Auction;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * AuctionManager triển khai theo mẫu thiết kế Singleton.
 * Quản lý trạng thái và thời gian của các phiên đấu giá đang diễn ra.
 */
@Service
public class AuctionManager {
    private static AuctionManager instance;
    private final Map<Long, Auction> activeAuctions = new ConcurrentHashMap<>();

    // Constructor public cho Spring, nhưng chúng ta lưu trữ instance static
    public AuctionManager() {
        instance = this;
    }

    public static AuctionManager getInstance() {
        if (instance == null) {
            instance = new AuctionManager();
        }
        return instance;
    }

    public void registerAuction(Auction auction) {
        activeAuctions.put(auction.getId(), auction);
        System.out.println("Auction registered: " + auction.getId());
    }

    public void removeAuction(Long auctionId) {
        activeAuctions.remove(auctionId);
    }

    public Map<Long, Auction> getActiveAuctions() {
        return activeAuctions;
    }
}
