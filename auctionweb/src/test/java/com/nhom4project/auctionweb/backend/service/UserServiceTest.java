package com.nhom4project.auctionweb.backend.service;

import com.nhom4project.auctionweb.backend.dto.SignupRequest;
import com.nhom4project.auctionweb.backend.model.Bidder;
import com.nhom4project.auctionweb.backend.model.Roles;
import com.nhom4project.auctionweb.backend.model.Seller;
import com.nhom4project.auctionweb.backend.model.User;
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
 * Unit Test cho UserService - Nhóm 4.
 */
@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @BeforeEach
    public void cleanDB() {
        auctionRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    // ============================================================
    // PHẦN 1: KIỂM TRA CÁC TRƯỜNG HỢP THÀNH CÔNG (HAPPY PATH)
    // ============================================================

    @Test
    @DisplayName("Đăng ký Bidder thành công")
    public void testRegisterBidder_Success() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setUsername("bidder_test");
        request.setPassword("pass12345"); // Phải ít nhất 8 ký tự theo validation
        request.setEmail("bidder@test.com");
        request.setFullname("Test Bidder");
        request.setRole("BIDDER");

        userService.registerUser(request);

        User user = userRepository.findByUsername("bidder_test");
        assertNotNull(user);
        assertEquals(Roles.BIDDER, user.getRole());
    }

    @Test
    @DisplayName("Đăng ký Seller thành công")
    public void testRegisterSeller_Success() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setUsername("seller_test");
        request.setPassword("pass12345");
        request.setEmail("seller@test.com");
        request.setFullname("Test Seller");
        request.setRole("SELLER");

        userService.registerUser(request);

        User user = userRepository.findByUsername("seller_test");
        assertNotNull(user);
        assertTrue(user instanceof Seller);
    }

    // ============================================================
    // PHẦN 2: KIỂM TRA CÁC LỖI (BUGS & EDGE CASES)
    // ============================================================

    @Test
    @DisplayName("Lỗi Bug: Đăng ký trùng Username")
    public void testDuplicateUsername_Failure() throws Exception {
        testRegisterBidder_Success();
        SignupRequest request = new SignupRequest();
        request.setUsername("bidder_test");
        assertThrows(Exception.class, () -> userService.registerUser(request));
    }

    @Test
    @DisplayName("Lỗi Bug: Tài khoản bị khóa không thể đăng nhập")
    public void testLockedUserLogin_Failure() throws Exception {
        testRegisterBidder_Success();
        User user = userRepository.findByUsername("bidder_test");
        userService.toggleLockUser(user.getId());

        assertThrows(Exception.class, () -> userService.authenticate("bidder_test", "pass12345"));
    }

    @Test
    @DisplayName("Kiểm tra status khóa: tài khoản bình thường")
    public void testIsUserLocked_NotLocked() throws Exception {
        testRegisterBidder_Success();
        User user = userRepository.findByUsername("bidder_test");
        assertFalse(userService.isUserLocked(user.getId()));
    }

    @Test
    @DisplayName("Kiểm tra status khóa: tài khoản bị khóa")
    public void testIsUserLocked_Locked() throws Exception {
        testRegisterBidder_Success();
        User user = userRepository.findByUsername("bidder_test");
        userService.toggleLockUser(user.getId());
        assertTrue(userService.isUserLocked(user.getId()));
    }

    @Test
    @DisplayName("Kiểm tra status khóa: tài khoản không tồn tại")
    public void testIsUserLocked_NotFound() {
        assertTrue(userService.isUserLocked(9999L));
    }
}
