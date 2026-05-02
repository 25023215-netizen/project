package com.nhom4project.auctionweb.service;

import com.nhom4project.auctionweb.data.model.Auction;
import com.nhom4project.auctionweb.data.model.AuctionStatus;
import com.nhom4project.auctionweb.data.repository.AuctionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduler tự động kết thúc phiên đấu giá khi hết thời gian.
 * Chạy mỗi 5 giây để kiểm tra các phiên RUNNING đã quá endTime.
 * Chuyển trạng thái: RUNNING -> FINISHED và xác định winner.
 */
@Service
@EnableScheduling
public class AuctionScheduler {

    private static final Logger log = LoggerFactory.getLogger(AuctionScheduler.class);

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Chạy mỗi 5 giây: tìm các auction đang RUNNING mà đã quá endTime.
     */
    @Scheduled(fixedRate = 5000)
    public void checkExpiredAuctions() {
        List<Auction> expired = auctionRepository
                .findByStatusAndEndTimeBefore(AuctionStatus.RUNNING, LocalDateTime.now());

        for (Auction auction : expired) {
            auction.setStatus(AuctionStatus.FINISHED);
            auctionRepository.save(auction);

            // Xóa khỏi active auctions
            AuctionManager.getInstance().updateStatus(auction.getId(), AuctionStatus.FINISHED);

            log.info("Auction {} '{}' finished. Winner: {}",
                    auction.getId(), auction.getTitle(),
                    auction.getWinner() != null ? auction.getWinner().getUsername() : "none");

            // Broadcast cho clients
            try {
                messagingTemplate.convertAndSend("/topic/auctions/" + auction.getId(),
                        java.util.Map.of(
                                "auctionId", auction.getId(),
                                "status", "FINISHED",
                                "currentPrice", auction.getCurrentPrice(),
                                "bidCount", auction.getBidCount(),
                                "winnerId", auction.getWinner() != null ? auction.getWinner().getId() : "",
                                "winnerName", auction.getWinner() != null ? auction.getWinner().getUsername() : ""
                        ));
                messagingTemplate.convertAndSend("/topic/auctions", "refresh");
            } catch (Exception e) {
                log.warn("Failed to broadcast auction end: {}", e.getMessage());
            }
        }

        // Tự động bắt đầu các phiên OPEN đã đến startTime
        List<Auction> openAuctions = auctionRepository.findByStatus(AuctionStatus.OPEN);
        for (Auction auction : openAuctions) {
            if (auction.getStartTime() != null && !LocalDateTime.now().isBefore(auction.getStartTime())) {
                auction.setStatus(AuctionStatus.RUNNING);
                auctionRepository.save(auction);
                AuctionManager.getInstance().registerAuction(auction);
                log.info("Auction {} '{}' auto-started", auction.getId(), auction.getTitle());
                try {
                    messagingTemplate.convertAndSend("/topic/auctions", "refresh");
                } catch (Exception ignored) {}
            }
        }
    }
}
