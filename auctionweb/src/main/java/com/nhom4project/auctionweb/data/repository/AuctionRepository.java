package com.nhom4project.auctionweb.data.repository;

import com.nhom4project.auctionweb.data.model.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
}
