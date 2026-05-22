package com.nhom4project.auctionweb.backend.service;

import com.nhom4project.auctionweb.backend.model.*;
import com.nhom4project.auctionweb.backend.repository.ItemRepository;
import com.nhom4project.auctionweb.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test cho ItemService - Nhóm 4.
 * Kiểm tra các tính năng: CRUD sản phẩm, xử lý ngoại lệ và logic Seller.
 */
@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private Seller seller;

    @BeforeEach
    void setUp() {
        long ts = System.nanoTime();
        seller = new Seller();
        seller.setUsername("seller_" + ts);
        seller.setPassword("pass12345");
        seller.setFullname("Test Seller");
        seller.setEmail("seller_" + ts + "@test.com");
        seller.setRole(Roles.SELLER);
        userRepository.save(seller);
    }

    // ============================================================
    // PHẦN 1: KIỂM TRA CÁC TRƯỜNG HỢP THÀNH CÔNG (HAPPY PATH)
    // ============================================================

    @Test
    @DisplayName("Tạo mới sản phẩm Electronics thành công")
    void testCreateElectronics_Success() {
        Item item = itemService.createItem("ELECTRONICS", "iPhone 15", "Apple Phone",
                20000000.0, seller.getId(), "Apple", "15 Pro");

        assertNotNull(item.getId());
        assertTrue(item instanceof Electronics);
        assertEquals("iPhone 15", item.getName());
    }

    @Test
    @DisplayName("Xóa sản phẩm thành công")
    void testDeleteItem_Success() {
        Item item = itemService.createItem("ELECTRONICS", "Test", "Desc", 1000.0, seller.getId(), null, null);
        Long id = item.getId();

        itemService.deleteItem(id);
        assertFalse(itemRepository.existsById(id));
    }

    // ============================================================
    // PHẦN 2: KIỂM TRA CÁC LỖI VÀ BUG TIỀM ẨN (EDGE CASES & BUGS)
    // ============================================================

    @Test
    @DisplayName("Lỗi Bug: Tạo sản phẩm với Seller không tồn tại")
    void testCreateWithInvalidSeller_Failure() {
        assertThrows(IllegalArgumentException.class, () -> 
            itemService.createItem("ELECTRONICS", "Name", "Desc", 1000.0, 9999L, null, null)
        );
    }

    @Test
    @DisplayName("Lỗi Bug: Người dùng không phải SELLER cố tình tạo sản phẩm")
    void testCreateWithNonSellerUser_Failure() {
        Bidder bidder = new Bidder();
        bidder.setUsername("hacker_bidder");
        bidder.setPassword("pass12345");
        bidder.setFullname("Hacker Bidder");
        bidder.setEmail("hacker@test.com");
        bidder.setRole(Roles.BIDDER);
        userRepository.save(bidder);

        assertThrows(IllegalArgumentException.class, () -> 
            itemService.createItem("ELECTRONICS", "Name", "Desc", 1000.0, bidder.getId(), null, null)
        );
    }
}
