package com.nhom4project.auctionweb.service;

import com.nhom4project.auctionweb.data.model.Auction;
import com.nhom4project.auctionweb.data.model.AuctionStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuctionManager {
    private static AuctionManager instance;
    private final Map<Long, Auction> activeAuctions = new ConcurrentHashMap<>();

    public AuctionManager() {
        instance = this;
    }

    public static synchronized AuctionManager getInstance() {
        if (instance == null) {
            instance = new AuctionManager();
        }
        return instance;
    }

    public void registerAuction(Auction auction) {
        if (auction != null && auction.getId() != null) {
            activeAuctions.put(auction.getId(), auction);
        }
    }

    public void updateStatus(Long auctionId, AuctionStatus status) {
        Auction auction = activeAuctions.get(auctionId);
        if (auction != null) {
            auction.setStatus(status);
            if (status == AuctionStatus.FINISHED) {
                activeAuctions.remove(auctionId);
            }
        }
    }

    public Auction getAuction(Long auctionId) {
        return activeAuctions.get(auctionId);
    }

    public void removeAuction(Long auctionId) {
        activeAuctions.remove(auctionId);
    }

    public Map<Long, Auction> getActiveAuctions() {
        return activeAuctions;
    }
}
