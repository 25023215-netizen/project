package com.nhom4project.auctionweb.service;

import com.nhom4project.auctionweb.data.model.*;
import com.nhom4project.auctionweb.data.repository.ItemRepository;
import com.nhom4project.auctionweb.data.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests cho ItemService và ItemFactory.
 * Kiểm tra: Factory Method pattern, CRUD operations.
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private Seller seller;

    @BeforeEach
    void setUp() {
        seller = new Seller();
        seller.setUsername("item_test_seller_" + System.nanoTime());
        seller.setPassword("password123");
        seller.setFullname("Item Test Seller");
        seller.setEmail("item_seller_" + System.nanoTime() + "@test.com");
        seller.setRole(Roles.SELLER);
        seller.setStoreName("Test Store");
        userRepository.save(seller);
    }

    // ==================== Factory Method Tests ====================

    @Test
    @DisplayName("Factory Method: tao Electronics")
    void testCreateElectronics() {
        Item item = ItemFactory.createItem("ELECTRONICS");
        assertInstanceOf(Electronics.class, item);
    }

    @Test
    @DisplayName("Factory Method: tao Art")
    void testCreateArt() {
        Item item = ItemFactory.createItem("ART");
        assertInstanceOf(Art.class, item);
    }

    @Test
    @DisplayName("Factory Method: tao Vehicle")
    void testCreateVehicle() {
        Item item = ItemFactory.createItem("VEHICLE");
        assertInstanceOf(Vehicle.class, item);
    }

    @Test
    @DisplayName("Factory Method: throw exception voi type khong hop le")
    void testCreateUnknownType() {
        assertThrows(IllegalArgumentException.class, () -> ItemFactory.createItem("FOOD"));
    }

    @Test
    @DisplayName("Factory Method: throw exception voi null type")
    void testCreateNullType() {
        assertThrows(IllegalArgumentException.class, () -> ItemFactory.createItem(null));
    }

    // ==================== ItemService CRUD Tests ====================

    @Test
    @DisplayName("Tao moi Electronics item qua ItemService")
    void testCreateElectronicsItem() {
        Item item = itemService.createItem("ELECTRONICS", "iPhone 15", "New phone",
                20000000.0, seller.getId(), "Apple", "iPhone 15 Pro");

        assertNotNull(item.getId());
        assertInstanceOf(Electronics.class, item);
        assertEquals("iPhone 15", item.getName());
        assertEquals(20000000.0, item.getStartingPrice());
        assertEquals("Apple", ((Electronics) item).getBrand());
        assertEquals("iPhone 15 Pro", ((Electronics) item).getModelName());
    }

    @Test
    @DisplayName("Tao moi Art item qua ItemService")
    void testCreateArtItem() {
        Item item = itemService.createItem("ART", "Starry Night", "Oil painting",
                5000000.0, seller.getId(), "Van Gogh", "Oil on Canvas");

        assertNotNull(item.getId());
        assertInstanceOf(Art.class, item);
        assertEquals("Van Gogh", ((Art) item).getArtist());
        assertEquals("Oil on Canvas", ((Art) item).getMedium());
    }

    @Test
    @DisplayName("Tao moi Vehicle item qua ItemService")
    void testCreateVehicleItem() {
        Item item = itemService.createItem("VEHICLE", "Honda Civic", "Sedan",
                500000000.0, seller.getId(), "Honda", "2024");

        assertNotNull(item.getId());
        assertInstanceOf(Vehicle.class, item);
        assertEquals("Honda", ((Vehicle) item).getManufacturer());
        assertEquals(2024, ((Vehicle) item).getReleaseYear());
    }

    @Test
    @DisplayName("Xoa item")
    void testDeleteItem() {
        Item item = itemService.createItem("ELECTRONICS", "Test Item", "Desc",
                100000.0, seller.getId(), null, null);

        Long id = item.getId();
        assertTrue(itemRepository.existsById(id));

        itemService.deleteItem(id);
        assertFalse(itemRepository.existsById(id));
    }

    @Test
    @DisplayName("Xoa item khong ton tai -> throw exception")
    void testDeleteNonExistentItem() {
        assertThrows(IllegalArgumentException.class, () -> itemService.deleteItem(99999L));
    }

    @Test
    @DisplayName("Lay danh sach items theo seller")
    void testListItemsBySeller() {
        itemService.createItem("ELECTRONICS", "Item 1", "Desc 1",
                100000.0, seller.getId(), null, null);
        itemService.createItem("ART", "Item 2", "Desc 2",
                200000.0, seller.getId(), null, null);

        var items = itemService.listItemsBySeller(seller.getId());
        assertTrue(items.size() >= 2);
    }
}
