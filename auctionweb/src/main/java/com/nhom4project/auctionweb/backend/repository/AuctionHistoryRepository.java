package com.nhom4project.auctionweb.backend.repository;

import com.nhom4project.auctionweb.backend.model.AuctionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuctionHistoryRepository extends JpaRepository<AuctionHistory, Long> {
}
