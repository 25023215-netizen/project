package com.nhom4project.auctionweb.service;

import com.nhom4project.auctionweb.data.model.*;
import com.nhom4project.auctionweb.data.repository.ItemRepository;
import com.nhom4project.auctionweb.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service quản lý sản phẩm đấu giá (Item).
 * Sử dụng Factory Method pattern để tạo đúng loại Item.
 */
@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

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
        item.setSeller((Seller) seller);

        // Gán các thuộc tính riêng của từng loại
        applyExtraFields(item, type, extraField1, extraField2);

        return itemRepository.save(item);
    }

    /**
     * Cập nhật thông tin Item.
     */
    public Item updateItem(Long id, String name, String description, Double startingPrice,
                           String extraField1, String extraField2) {
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
        itemRepository.deleteById(id);
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
