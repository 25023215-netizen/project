package com.nhom4project.auctionweb.data;

import com.nhom4project.auctionweb.data.model.*;
import com.nhom4project.auctionweb.data.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Khởi tạo dữ liệu mẫu khi database trống.
 * Tạo các tài khoản mẫu (Seller, Bidder, Admin) và sản phẩm/phiên đấu giá.
 */
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

        // === Tạo Users ===

        // Seller account
        Seller seller = new Seller();
        seller.setUsername("seller1");
        seller.setPassword("password123");
        seller.setFullname("John Seller");
        seller.setEmail("seller@example.com");
        seller.setRole(Roles.SELLER);
        seller.setStoreName("John's Tech");
        userRepository.save(seller);

        // Bidder accounts
        Bidder bidder1 = new Bidder();
        bidder1.setUsername("bidder1");
        bidder1.setPassword("password123");
        bidder1.setFullname("Alice Bidder");
        bidder1.setEmail("bidder1@example.com");
        bidder1.setRole(Roles.BIDDER);
        userRepository.save(bidder1);

        Bidder bidder2 = new Bidder();
        bidder2.setUsername("bidder2");
        bidder2.setPassword("password123");
        bidder2.setFullname("Bob Bidder");
        bidder2.setEmail("bidder2@example.com");
        bidder2.setRole(Roles.BIDDER);
        userRepository.save(bidder2);

        // Admin account
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword("admin12345");
        admin.setFullname("System Admin");
        admin.setEmail("admin@example.com");
        admin.setRole(Roles.ADMIN);
        admin.setAdminLevel("SUPER");
        userRepository.save(admin);

        // === Tạo Items (Factory Method used via DataInitializer) ===

        Electronics laptop = new Electronics();
        laptop.setName("Gaming Laptop ROG Strix");
        laptop.setDescription("Laptop gaming hieu nang cao, RTX 4060, 16GB RAM");
        laptop.setStartingPrice(25000000.0);
        laptop.setCurrentPrice(25000000.0);
        laptop.setSeller(seller);
        laptop.setBrand("Asus");
        laptop.setModelName("ROG Strix G16");
        itemRepository.save(laptop);

        Art painting = new Art();
        painting.setName("Tranh Son Dau - Ho Guom");
        painting.setDescription("Tranh ve tay tren canvas, kich thuoc 80x60cm");
        painting.setStartingPrice(3000000.0);
        painting.setCurrentPrice(3000000.0);
        painting.setSeller(seller);
        painting.setArtist("Nguyen Van A");
        painting.setMedium("Oil on Canvas");
        itemRepository.save(painting);

        Vehicle bike = new Vehicle();
        bike.setName("Honda Wave Alpha 2023");
        bike.setDescription("Xe may con moi 95%, bao duong dinh ky, it su dung");
        bike.setStartingPrice(12000000.0);
        bike.setCurrentPrice(12000000.0);
        bike.setSeller(seller);
        bike.setManufacturer("Honda");
        bike.setReleaseYear(2023);
        itemRepository.save(bike);

        // === Tạo Auctions ===

        Auction a1 = new Auction();
        a1.setTitle("Gaming Laptop ROG Strix");
        a1.setCategory("Electronics");
        a1.setDescription(laptop.getDescription());
        a1.setStartingPrice(new BigDecimal("25000000"));
        a1.setCurrentPrice(new BigDecimal("25000000"));
        a1.setBidCount(0);
        a1.setItem(laptop);
        a1.setSeller(seller);
        a1.setStartTime(LocalDateTime.now());
        a1.setEndTime(LocalDateTime.now().plusDays(2));
        a1.setStatus(AuctionStatus.RUNNING);
        auctionRepository.save(a1);

        Auction a2 = new Auction();
        a2.setTitle("Tranh Son Dau - Ho Guom");
        a2.setCategory("Art");
        a2.setDescription(painting.getDescription());
        a2.setStartingPrice(new BigDecimal("3000000"));
        a2.setCurrentPrice(new BigDecimal("3000000"));
        a2.setBidCount(0);
        a2.setItem(painting);
        a2.setSeller(seller);
        a2.setStartTime(LocalDateTime.now());
        a2.setEndTime(LocalDateTime.now().plusDays(3));
        a2.setStatus(AuctionStatus.RUNNING);
        auctionRepository.save(a2);

        Auction a3 = new Auction();
        a3.setTitle("Honda Wave Alpha 2023");
        a3.setCategory("Vehicle");
        a3.setDescription(bike.getDescription());
        a3.setStartingPrice(new BigDecimal("12000000"));
        a3.setCurrentPrice(new BigDecimal("12000000"));
        a3.setBidCount(0);
        a3.setItem(bike);
        a3.setSeller(seller);
        a3.setStartTime(LocalDateTime.now().plusHours(1));
        a3.setEndTime(LocalDateTime.now().plusDays(4));
        a3.setStatus(AuctionStatus.OPEN);
        auctionRepository.save(a3);

        System.out.println("=== Seed data initialized! ===");
        System.out.println("  Seller: seller1 / password123");
        System.out.println("  Bidder: bidder1 / password123");
        System.out.println("  Bidder: bidder2 / password123");
        System.out.println("  Admin:  admin / admin12345");
    }
}
