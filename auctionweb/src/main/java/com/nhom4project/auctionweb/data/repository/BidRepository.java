package com.nhom4project.auctionweb.data.repository;

import com.nhom4project.auctionweb.data.model.BidTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<BidTransaction, Long> {
    List<BidTransaction> findByAuctionIdOrderByBidTimeDesc(Long auctionId);
}
