package com.nhom4project.auctionweb.data.repository;

import com.nhom4project.auctionweb.data.model.AutoBidConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutoBidConfigRepository extends JpaRepository<AutoBidConfig, Long> {

    /**
     * Lấy tất cả auto-bid config đang active của một auction, sắp xếp theo thời gian đăng ký.
     */
    List<AutoBidConfig> findByAuctionIdAndActiveTrueOrderByRegisteredAtAsc(Long auctionId);

    /**
     * Tìm auto-bid config của một bidder trên một auction cụ thể.
     */
    Optional<AutoBidConfig> findByAuctionIdAndBidderId(Long auctionId, Long bidderId);
}
