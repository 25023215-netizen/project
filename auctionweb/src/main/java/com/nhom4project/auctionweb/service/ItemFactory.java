package com.nhom4project.auctionweb.service;

import com.nhom4project.auctionweb.data.model.*;

/**
 * Factory Method Pattern: tạo các loại Item khác nhau dựa trên type string.
 */
public class ItemFactory {

    public static Item createItem(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Item type must not be null");
        }
        return switch (type.toUpperCase()) {
            case "ELECTRONICS" -> new Electronics();
            case "ART" -> new Art();
            case "VEHICLE" -> new Vehicle();
            default -> throw new IllegalArgumentException("Unknown item type: " + type);
        };
    }
}
