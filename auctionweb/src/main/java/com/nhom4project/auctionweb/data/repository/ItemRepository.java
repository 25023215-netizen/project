package com.nhom4project.auctionweb.data.repository;

import com.nhom4project.auctionweb.data.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}
