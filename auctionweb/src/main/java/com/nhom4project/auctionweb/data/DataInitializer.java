package com.nhom4project.auctionweb.data;

import com.nhom4project.auctionweb.data.model.*;
import com.nhom4project.auctionweb.data.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) return;

        // Create Seller
        Seller seller = new Seller();
        seller.setUsername("seller1");
        seller.setPassword("password");
        seller.setFullname("John Seller");
        seller.setEmail("seller@example.com");
        seller.setRole(Roles.SELLER);
        seller.setStoreName("John's Tech");
        userRepository.save(seller);

        // Create Items
        Electronics laptop = new Electronics();
        laptop.setName("Gaming Laptop");
        laptop.setDescription("High performance gaming laptop");
        laptop.setStartingPrice(1000.0);
        laptop.setCurrentPrice(1000.0);
        laptop.setSeller(seller);
        laptop.setBrand("Asus");
        laptop.setModelName("ROG");
        itemRepository.save(laptop);

        Art painting = new Art();
        painting.setName("Starry Night Replica");
        painting.setDescription("Beautiful oil painting");
        painting.setStartingPrice(500.0);
        painting.setCurrentPrice(500.0);
        painting.setSeller(seller);
        painting.setArtist("Van Gogh Copycat");
        painting.setMedium("Oil on Canvas");
        itemRepository.save(painting);

        // Create Auctions
        Auction a1 = new Auction();
        a1.setItem(laptop);
        a1.setStartTime(LocalDateTime.now());
        a1.setEndTime(LocalDateTime.now().plusDays(1));
        a1.setStatus(AuctionStatus.RUNNING);
        auctionRepository.save(a1);

        Auction a2 = new Auction();
        a2.setItem(painting);
        a2.setStartTime(LocalDateTime.now());
        a2.setEndTime(LocalDateTime.now().plusHours(5));
        a2.setStatus(AuctionStatus.RUNNING);
        auctionRepository.save(a2);

        System.out.println("Seed data initialized!");
    }
}
