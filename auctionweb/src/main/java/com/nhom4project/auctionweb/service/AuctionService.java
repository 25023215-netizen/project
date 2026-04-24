package com.nhom4project.auctionweb.service;

import com.nhom4project.auctionweb.data.model.Auction;
import com.nhom4project.auctionweb.data.model.AuctionStatus;
import com.nhom4project.auctionweb.data.model.BidTransaction;
import com.nhom4project.auctionweb.data.model.Bidder;
import com.nhom4project.auctionweb.data.repository.AuctionRepository;
import com.nhom4project.auctionweb.data.repository.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuctionService {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private AuctionManager auctionManager;

    public List<Auction> getAllAuctions() {
        return auctionRepository.findAll();
    }

    public Auction getAuctionById(Long id) {
        return auctionRepository.findById(id).orElse(null);
    }

    @Transactional
    public void placeBid(Long auctionId, Bidder bidder, Double amount) throws Exception {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new Exception("Auction not found"));

        if (auction.getStatus() != AuctionStatus.OPEN && auction.getStatus() != AuctionStatus.RUNNING) {
            throw new Exception("Auction is not accepting bids");
        }

        if (amount <= auction.getItem().getCurrentPrice()) {
            throw new Exception("Bid amount must be higher than current price");
        }

        // Create transaction
        BidTransaction bid = new BidTransaction();
        bid.setAuction(auction);
        bid.setBidder(bidder);
        bid.setAmount(amount);
        bid.setBidTime(LocalDateTime.now());
        bidRepository.save(bid);

        // Update auction
        auction.getItem().setCurrentPrice(amount);
        auction.setWinner(bidder);
        auctionRepository.save(auction);

        // Notify manager (Singleton)
        auctionManager.registerAuction(auction);
    }
}
