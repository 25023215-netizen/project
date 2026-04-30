package com.nhom4project.auctionweb.service;

import com.nhom4project.auctionweb.data.model.Auction;
import com.nhom4project.auctionweb.data.model.AuctionStatus;
import com.nhom4project.auctionweb.data.model.Bidder;
import com.nhom4project.auctionweb.data.repository.AuctionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuctionService {
    @Autowired
    private AuctionRepository auctionRepository;

    @PostConstruct
    public void seedAuctions() {
        if (auctionRepository.count() > 0) {
            return;
        }

        auctionRepository.save(createAuction(
                "iPhone 15 Pro Max 256GB",
                "Electronics",
                "May moi full box, mau Titan Den.",
                new BigDecimal("25000000"),
                18,
                AuctionStatus.RUNNING,
                LocalDateTime.now().plusDays(2)
        ));
        auctionRepository.save(createAuction(
                "Tranh Son Dau - Ho Guom",
                "Art",
                "Tranh ve tay kich thuoc 80x60cm.",
                new BigDecimal("5200000"),
                9,
                AuctionStatus.RUNNING,
                LocalDateTime.now().plusDays(3)
        ));
        auctionRepository.save(createAuction(
                "Honda Wave Alpha 2023",
                "Vehicle",
                "Xe con moi 95%, bao duong tot.",
                new BigDecimal("15000000"),
                4,
                AuctionStatus.OPEN,
                LocalDateTime.now().plusDays(4)
        ));
    }

    public List<Auction> listAuctions() {
        return auctionRepository.findAll();
    }

    public void startAuction(Long id) {
        Auction auction = findAuction(id);
        auction.setStatus(AuctionStatus.RUNNING);
        auctionRepository.save(auction);
        AuctionManager.getInstance().registerAuction(auction);
    }

    public synchronized boolean placeBid(Long id, Bidder bidder, BigDecimal amount) {
        Auction auction = findAuction(id);
        if (auction.getStatus() != AuctionStatus.RUNNING || amount.compareTo(auction.getCurrentPrice()) <= 0) {
            return false;
        }
        auction.setCurrentPrice(amount);
        auction.setBidCount(auction.getBidCount() + 1);
        auctionRepository.save(auction);
        AuctionManager.getInstance().registerAuction(auction);
        return true;
    }

    public void endAuction(Long id) {
        Auction auction = findAuction(id);
        auction.setStatus(AuctionStatus.FINISHED);
        auctionRepository.save(auction);
        AuctionManager.getInstance().updateStatus(id, AuctionStatus.FINISHED);
    }

    private Auction findAuction(Long id) {
        return auctionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Auction not found"));
    }

    private Auction createAuction(String title, String category, String description, BigDecimal price,
                                  int bidCount, AuctionStatus status, LocalDateTime endTime) {
        Auction auction = new Auction();
        auction.setTitle(title);
        auction.setCategory(category);
        auction.setDescription(description);
        auction.setCurrentPrice(price);
        auction.setBidCount(bidCount);
        auction.setStatus(status);
        auction.setEndTime(endTime);
        return auction;
    }
}
