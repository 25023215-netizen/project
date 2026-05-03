package com.nhom4project.auctionweb.data.repository;

import com.nhom4project.auctionweb.data.model.Auction;
import com.nhom4project.auctionweb.data.model.AuctionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {

    List<Auction> findByStatus(AuctionStatus status);

    /**
     * Tìm các phiên đấu giá đang RUNNING và đã quá endTime -> cần kết thúc.
     */
    List<Auction> findByStatusAndEndTimeBefore(AuctionStatus status, LocalDateTime time);

    List<Auction> findBySellerId(Long sellerId);
}
