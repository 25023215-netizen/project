package com.nhom4project.auctionweb.backend.service;

import com.nhom4project.auctionweb.backend.model.*;
import com.nhom4project.auctionweb.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

/**
 * Service quản lý sản phẩm đấu giá (Item).
 * Sử dụng Factory Method pattern để tạo đúng loại Item.
 */
@Service
@Transactional
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private AutoBidConfigRepository autoBidConfigRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public List<Item> listItems() {
        return itemRepository.findAll();
    }

    public List<Item> listItemsBySeller(Long sellerId) {
        return itemRepository.findBySellerId(sellerId);
    }

    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    /**
     * Tạo mới Item sử dụng Factory Method pattern.
     */
    public Item createItem(String type, String name, String description,
                           Double startingPrice, Long sellerId,
                           String extraField1, String extraField2) {

        if (startingPrice == null || startingPrice <= 0) {
            throw new IllegalArgumentException("Giá khởi điểm phải lớn hơn 0");
        }

        // Factory Method: tạo đúng loại item
        Item item = ItemFactory.createItem(type);
        item.setName(name);
        item.setDescription(description);
        item.setStartingPrice(startingPrice);
        item.setCurrentPrice(startingPrice);

        // Gán seller
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));
        if (!(seller instanceof Seller)) {
            throw new IllegalArgumentException("User is not a Seller");
        }
        if (seller.isLocked()) {
            throw new IllegalArgumentException("Tài khoản này đã bị khoá và sẽ không thể thực hiện được hành động gì cả");
        }
        item.setSeller((Seller) seller);

        // Gán các thuộc tính riêng của từng loại
        applyExtraFields(item, type, extraField1, extraField2);

        Item savedItem = itemRepository.save(item);

        // Auto-create Auction in OPEN status
        Auction auction = new Auction();
        auction.setItem(savedItem);
        auction.setTitle(savedItem.getName());
        auction.setCategory(type);
        auction.setDescription(savedItem.getDescription());
        auction.setStartingPrice(java.math.BigDecimal.valueOf(savedItem.getStartingPrice()));
        auction.setCurrentPrice(java.math.BigDecimal.valueOf(savedItem.getStartingPrice()));
        auction.setBidCount(0);
        auction.setSeller((Seller) seller);
        auction.setStartTime(java.time.LocalDateTime.now());
        auction.setEndTime(java.time.LocalDateTime.now().plusDays(3)); // default duration is 3 days
        auction.setStatus(AuctionStatus.OPEN);
        auctionRepository.save(auction);

        // Broadcast refresh signal to clients
        try {
            messagingTemplate.convertAndSend("/topic/auctions", "refresh");
        } catch (Exception e) {
            // Ignore/Log
        }

        return savedItem;
    }

    /**
     * Cập nhật thông tin Item.
     */
    public Item updateItem(Long id, String name, String description, Double startingPrice,
                           String extraField1, String extraField2) {
        if (startingPrice == null || startingPrice <= 0) {
            throw new IllegalArgumentException("Giá khởi điểm phải lớn hơn 0");
        }

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        item.setName(name);
        item.setDescription(description);
        item.setStartingPrice(startingPrice);
        // Chỉ cập nhật currentPrice nếu chưa có bid nào
        if (item.getCurrentPrice().equals(item.getStartingPrice())) {
            item.setCurrentPrice(startingPrice);
        }

        applyExtraFields(item, item.getClass().getAnnotation(
                jakarta.persistence.DiscriminatorValue.class).value(), extraField1, extraField2);

        return itemRepository.save(item);
    }

    /**
     * Xóa Item theo ID.
     */
    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new IllegalArgumentException("Item not found");
        }

        // First delete any associated Auction
        auctionRepository.findByItemId(id).ifPresent(auction -> {
            // Delete auto-bid configs
            autoBidConfigRepository.deleteByAuctionId(auction.getId());
            // Delete bids
            bidRepository.deleteByAuctionId(auction.getId());
            // Remove from manager
            AuctionManager.getInstance().removeAuction(auction.getId());
            // Delete the auction
            auctionRepository.delete(auction);
        });

        itemRepository.deleteById(id);

        // Notify client to refresh dashboard
        try {
            messagingTemplate.convertAndSend("/topic/auctions", "refresh");
        } catch (Exception e) {
            // Ignore/Log
        }
    }

    /**
     * Áp dụng thuộc tính riêng cho từng loại Item (Polymorphism).
     */
    private void applyExtraFields(Item item, String type, String field1, String field2) {
        switch (type.toUpperCase()) {
            case "ELECTRONICS" -> {
                Electronics e = (Electronics) item;
                if (field1 != null) e.setBrand(field1);
                if (field2 != null) e.setModelName(field2);
            }
            case "ART" -> {
                Art a = (Art) item;
                if (field1 != null) a.setArtist(field1);
                if (field2 != null) a.setMedium(field2);
            }
            case "VEHICLE" -> {
                Vehicle v = (Vehicle) item;
                if (field1 != null) v.setManufacturer(field1);
                if (field2 != null) {
                    try { v.setReleaseYear(Integer.parseInt(field2)); } catch (NumberFormatException ignored) {}
                }
            }
        }
    }
}




