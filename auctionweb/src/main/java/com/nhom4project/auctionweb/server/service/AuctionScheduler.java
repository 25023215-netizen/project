package com.nhom4project.auctionweb.server.service;

import com.nhom4project.auctionweb.server.model.Auction;
import com.nhom4project.auctionweb.server.model.AuctionStatus;
import com.nhom4project.auctionweb.server.repository.AuctionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    //Kết thúc phiên đầu giá sớm theo yêu cầu của client
    public ResponseEntity<?> endAuctionEarly(Long auctionId, Long userId, String role) {
        Optional<Auction> auctionOpt = auctionRepository.findById(auctionId);
        if (auctionOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Auction not found");
        }

        Auction auction = auctionOpt.get();

        // Kiểm tra quyền: chỉ admin hoặc chủ phiên được kết thúc sớm
        if (!"ADMIN".equalsIgnoreCase(role) && !auction.getSeller().getId().equals(userId)) {
            return ResponseEntity.status(403).body("Bạn không có quyền kết thúc phiên này");
        }

        if (auction.getStatus() == AuctionStatus.RUNNING) {
            auction.setStatus(AuctionStatus.FINISHED);
            auctionRepository.save(auction);
            AuctionManager.getInstance().updateStatus(auction.getId(), AuctionStatus.FINISHED);

            log.info("Auction {} '{}' ended early by user {} (role={})",
                    auction.getId(), auction.getTitle(), userId, role);

            try {
                messagingTemplate.convertAndSend("/topic/auctions/" + auction.getId(),
                        Map.of(
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

            return ResponseEntity.ok("Auction ended early");
        } else {
            return ResponseEntity.badRequest().body("Auction is not running");
        }
    }
}
