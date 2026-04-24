package com.nhom4project.auctionweb.service;

import com.nhom4project.auctionweb.data.model.Auction;
import com.nhom4project.auctionweb.data.model.AuctionStatus;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AuctionManager triển khai theo mẫu thiết kế Singleton.
 * Chịu trách nhiệm quản lý trạng thái của các phiên đấu giá đang diễn ra.
 */
public class AuctionManager {
    private static AuctionManager instance;
    private final Map<Long, Auction> activeAuctions;

    private AuctionManager() {
        this.activeAuctions = new ConcurrentHashMap<>();
    }

    public static synchronized AuctionManager getInstance() {
        if (instance == null) {
            instance = new AuctionManager();
        }
        return instance;
    }

    public void registerAuction(Auction auction) {
        activeAuctions.put(auction.getId(), auction);
    }

    public void updateStatus(Long auctionId, AuctionStatus status) {
        Auction auction = activeAuctions.get(auctionId);
        if (auction != null) {
            auction.setStatus(status);
            // Thêm logic xử lý khi trạng thái thay đổi (ví dụ: thông báo cho người dùng)
            if (status == AuctionStatus.FINISHED) {
                activeAuctions.remove(auctionId);
            }
        }
    }

    public Auction getAuction(Long auctionId) {
        return activeAuctions.get(auctionId);
    }
}
