package com.nhom4project.auctionweb.controller;

import com.nhom4project.auctionweb.data.model.Item;
import com.nhom4project.auctionweb.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller quản lý sản phẩm đấu giá (dành cho Seller).
 */
@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public List<Item> listItems() {
        return itemService.listItems();
    }

    @GetMapping("/seller/{sellerId}")
    public List<Item> listItemsBySeller(@PathVariable Long sellerId) {
        return itemService.listItemsBySeller(sellerId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItem(@PathVariable Long id) {
        return itemService.getItemById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Tạo mới Item.
     * Body JSON: { "type": "ELECTRONICS", "name": "...", "description": "...",
     *              "startingPrice": 1000.0, "sellerId": 1, "extraField1": "Asus", "extraField2": "ROG" }
     */
    @PostMapping
    public ResponseEntity<?> createItem(@RequestBody Map<String, Object> body) {
        try {
            String type = (String) body.get("type");
            String name = (String) body.get("name");
            String description = (String) body.get("description");
            Double startingPrice = Double.valueOf(body.get("startingPrice").toString());
            Long sellerId = Long.valueOf(body.get("sellerId").toString());
            String extraField1 = (String) body.getOrDefault("extraField1", null);
            String extraField2 = (String) body.getOrDefault("extraField2", null);

            Item item = itemService.createItem(type, name, description, startingPrice, sellerId, extraField1, extraField2);
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            String name = (String) body.get("name");
            String description = (String) body.get("description");
            Double startingPrice = Double.valueOf(body.get("startingPrice").toString());
            String extraField1 = (String) body.getOrDefault("extraField1", null);
            String extraField2 = (String) body.getOrDefault("extraField2", null);

            Item item = itemService.updateItem(id, name, description, startingPrice, extraField1, extraField2);
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        try {
            itemService.deleteItem(id);
            return ResponseEntity.ok("Item deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
