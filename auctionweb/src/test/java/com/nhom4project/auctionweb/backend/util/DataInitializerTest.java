package com.nhom4project.auctionweb.backend.util;

import com.nhom4project.auctionweb.backend.repository.AuctionRepository;
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
 * Unit/Integration Test cho DataInitializer - Nhóm 4.
 * Kiểm tra xem dữ liệu mẫu có được khởi tạo chính xác khi DB trống hay không.
 */
@SpringBootTest(classes = com.nhom4project.auctionweb.backend.BackendApplication.class)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DataInitializerTest {

    @Autowired
    private DataInitializer dataInitializer;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @BeforeEach
    public void setup() {
        auctionRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Khởi tạo dữ liệu mẫu thành công khi database trống")
    public void testDataInitialization_Success() throws Exception {
        // Database đang trống
        assertEquals(0, userRepository.count());
        assertEquals(0, itemRepository.count());
        assertEquals(0, auctionRepository.count());

        // Chạy data initializer
        dataInitializer.run();

        // Kiểm tra xem dữ liệu mẫu đã được khởi tạo
        assertTrue(userRepository.count() > 0);
        assertTrue(itemRepository.count() > 0);
        assertTrue(auctionRepository.count() > 0);

        // Xác minh sự tồn tại của các tài khoản mặc định
        assertNotNull(userRepository.findByUsername("seller1"));
        assertNotNull(userRepository.findByUsername("bidder1"));
        assertNotNull(userRepository.findByUsername("bidder2"));
        assertNotNull(userRepository.findByUsername("admin"));
    }

    @Test
    @DisplayName("Không ghi đè dữ liệu mẫu khi database đã có dữ liệu")
    public void testDataInitialization_NoDuplicate() throws Exception {
        // Chạy lần đầu tiên để tạo dữ liệu mẫu
        dataInitializer.run();
        long initialUserCount = userRepository.count();
        long initialItemCount = itemRepository.count();
        long initialAuctionCount = auctionRepository.count();

        // Chạy lần 2
        dataInitializer.run();

        // Đảm bảo số lượng bản ghi không tăng lên (không bị trùng lặp)
        assertEquals(initialUserCount, userRepository.count());
        assertEquals(initialItemCount, itemRepository.count());
        assertEquals(initialAuctionCount, auctionRepository.count());
    }
}
