package com.nhom4project.auctionweb.server.repository;

import com.nhom4project.auctionweb.server.model.AuctionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuctionHistoryRepository extends JpaRepository<AuctionHistory, Long> {
}
