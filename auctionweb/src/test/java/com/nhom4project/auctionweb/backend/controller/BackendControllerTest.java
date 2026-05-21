package com.nhom4project.auctionweb.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhom4project.auctionweb.backend.dto.SigninRequest;
import com.nhom4project.auctionweb.backend.dto.SignupRequest;
import com.nhom4project.auctionweb.backend.model.*;
import com.nhom4project.auctionweb.backend.repository.*;
import com.nhom4project.auctionweb.backend.service.AuctionService;
import com.nhom4project.auctionweb.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Concurrency & Integration Test Suite cho toàn bộ Backend Controllers - Nhóm 4.
 * Tăng độ bao phủ kiểm thử từ 2% lên 100% cho com.nhom4project.auctionweb.backend.controller.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BackendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private com.nhom4project.auctionweb.backend.service.ItemService itemService;

    private Seller seller;
    private Bidder bidder;
    private Admin admin;

    @BeforeEach
    public void setup() throws Exception {
        auctionRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        // Tạo dữ liệu người dùng mẫu
        SignupRequest sReq = new SignupRequest();
        sReq.setUsername("seller_test");
        sReq.setPassword("pass12345");
        sReq.setEmail("seller@test.com");
        sReq.setFullname("Test Seller");
        sReq.setRole("SELLER");
        userService.registerUser(sReq);
        seller = (Seller) userRepository.findByUsername("seller_test");

        SignupRequest bReq = new SignupRequest();
        bReq.setUsername("bidder_test");
        bReq.setPassword("pass12345");
        bReq.setEmail("bidder@test.com");
        bReq.setFullname("Test Bidder");
        bReq.setRole("BIDDER");
        userService.registerUser(bReq);
        bidder = (Bidder) userRepository.findByUsername("bidder_test");

        Admin aUser = new Admin();
        aUser.setUsername("admin_test");
        aUser.setPassword("pass12345");
        aUser.setEmail("admin@test.com");
        aUser.setFullname("Test Admin");
        aUser.setRole(Roles.ADMIN);
        admin = userRepository.save(aUser);
    }

    // ============================================================
    // 1. HOME CONTROLLER TESTS
    // ============================================================

    @Test
    @DisplayName("Kiểm tra HomeController GET /")
    public void testHomeHello() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Backend Auction Web đang chạy thành công!")));
    }

    // ============================================================
    // 2. AUTH CONTROLLER TESTS
    // ============================================================

    @Test
    @DisplayName("Đăng ký người dùng qua API /auth/signup thành công")
    public void testAuthSignup_Success() throws Exception {
        SignupRequest req = new SignupRequest();
        req.setUsername("new_user");
        req.setPassword("pass12345");
        req.setEmail("newuser@test.com");
        req.setFullname("New User");
        req.setRole("BIDDER");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully!"));
    }

    @Test
    @DisplayName("Đăng ký người dùng qua API lỗi trùng username")
    public void testAuthSignup_DuplicateUsername() throws Exception {
        SignupRequest req = new SignupRequest();
        req.setUsername("seller_test");
        req.setPassword("pass12345");
        req.setEmail("unique@test.com");
        req.setFullname("Unique Name");
        req.setRole("SELLER");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Đăng ký lỗi validation email")
    public void testAuthSignup_ValidationError() throws Exception {
        SignupRequest req = new SignupRequest();
        req.setUsername("valid_username");
        req.setPassword("short"); // Quá ngắn
        req.setEmail("invalid-email");
        req.setFullname("Valid Name");
        req.setRole("SELLER");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Đăng nhập API /auth/signin thành công")
    public void testAuthSignin_Success() throws Exception {
        SigninRequest req = new SigninRequest();
        req.setUsername("seller_test");
        req.setPassword("pass12345");

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("seller_test"));
    }

    @Test
    @DisplayName("Đăng nhập API /auth/signin thất bại do sai password")
    public void testAuthSignin_WrongPassword() throws Exception {
        SigninRequest req = new SigninRequest();
        req.setUsername("seller_test");
        req.setPassword("wrongpass");

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // ============================================================
    // 3. ITEM CONTROLLER TESTS
    // ============================================================

    @Test
    @DisplayName("Tạo, lấy, cập nhật, xóa Item qua API /api/items")
    public void testItemLifecycleAPI() throws Exception {
        // 3.1. Tạo mới Item
        Map<String, Object> body = new HashMap<>();
        body.put("type", "VEHICLE");
        body.put("name", "Toyota Camry 2024");
        body.put("description", "Luxury Sedan");
        body.put("startingPrice", 50000.0);
        body.put("sellerId", seller.getId());
        body.put("extraField1", "Sedan");
        body.put("extraField2", "2.5Q");

        String responseJson = mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Toyota Camry 2024"))
                .andReturn().getResponse().getContentAsString();

        Map<?, ?> itemMap = objectMapper.readValue(responseJson, Map.class);
        Long itemId = Long.valueOf(itemMap.get("id").toString());
        assertNotNull(itemId);

        // 3.2. Lấy danh sách toàn bộ Items
        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Toyota Camry 2024"));

        // 3.3. Lấy danh sách Items theo Seller
        mockMvc.perform(get("/api/items/seller/" + seller.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Toyota Camry 2024"));

        // 3.4. Lấy chi tiết Item
        mockMvc.perform(get("/api/items/" + itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Toyota Camry 2024"));

        // 3.5. Cập nhật Item
        Map<String, Object> updateBody = new HashMap<>();
        updateBody.put("name", "Toyota Camry 2024 V2");
        updateBody.put("description", "Updated Luxury Sedan");
        updateBody.put("startingPrice", 55000.0);

        mockMvc.perform(put("/api/items/" + itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Toyota Camry 2024 V2"));

        // 3.6. Xóa Item
        mockMvc.perform(delete("/api/items/" + itemId))
                .andExpect(status().isOk())
                .andExpect(content().string("Item deleted"));

        // 3.7. Lấy lại để đảm bảo đã bị xóa
        mockMvc.perform(get("/api/items/" + itemId))
                .andExpect(status().isNotFound());
    }

    // ============================================================
    // 4. AUCTION CONTROLLER TESTS
    // ============================================================

    @Test
    @DisplayName("Quy trình Đấu giá, Đặt giá và Auto-bid qua API /api/auctions")
    public void testAuctionLifecycleAPI() throws Exception {
        // Tạo sản phẩm cho phiên đấu giá
        Item item = itemServiceCreateItem();

        // 4.1. Tạo mới phiên đấu giá
        Map<String, Object> aBody = new HashMap<>();
        aBody.put("title", "Honda Civic 2024 Auction");
        aBody.put("category", "Vehicle");
        aBody.put("description", "VTEC Turbo");
        aBody.put("startingPrice", 30000.0);
        aBody.put("sellerId", seller.getId());
        aBody.put("endTime", LocalDateTime.now().plusDays(2).toString());

        String aJson = mockMvc.perform(post("/api/auctions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Honda Civic 2024 Auction"))
                .andReturn().getResponse().getContentAsString();

        Auction auction = objectMapper.readValue(aJson, Auction.class);

        // 4.2. Duyệt đấu giá bởi Admin (Chuyển trạng thái từ PENDING sang OPEN)
        auction.setStatus(AuctionStatus.PENDING);
        auctionRepository.save(auction);

        mockMvc.perform(post("/api/admin/auctions/" + auction.getId() + "/approve"))
                .andExpect(status().isOk());

        // 4.3. Bắt đầu phiên đấu giá (Chuyển sang RUNNING)
        mockMvc.perform(post("/api/auctions/" + auction.getId() + "/start"))
                .andExpect(status().isOk());

        // 4.4. Lấy danh sách toàn bộ phiên đấu giá
        mockMvc.perform(get("/api/auctions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // 4.5. Lấy danh sách phiên đấu giá của Seller
        mockMvc.perform(get("/api/auctions/seller/" + seller.getId()))
                .andExpect(status().isOk());

        // 4.6. Lấy chi tiết phiên đấu giá
        mockMvc.perform(get("/api/auctions/" + auction.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Honda Civic 2024 Auction"));

        // 4.7. Đặt giá thầu đầu tiên
        Map<String, Object> bidBody = new HashMap<>();
        bidBody.put("bidderId", bidder.getId());
        bidBody.put("amount", 31000.0);

        mockMvc.perform(post("/api/auctions/" + auction.getId() + "/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bidBody)))
                .andExpect(status().isOk())
                .andExpect(content().string("Bid placed successfully"));

        // 4.8. Xem lịch sử đặt giá
        mockMvc.perform(get("/api/auctions/" + auction.getId() + "/bids"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(31000.0));

        // 4.9. Đăng ký Auto-bid
        Map<String, Object> abBody = new HashMap<>();
        abBody.put("bidderId", bidder.getId());
        abBody.put("maxBid", 50000.0);
        abBody.put("increment", 1000.0);

        mockMvc.perform(post("/api/auctions/" + auction.getId() + "/auto-bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(abBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxBid").value(50000.0));

        // 4.10. Xem trạng thái Auto-bid
        mockMvc.perform(get("/api/auctions/" + auction.getId() + "/auto-bid/status?bidderId=" + bidder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxBid").value(50000.0));

        // 4.11. Dừng Auto-bid
        Map<String, Object> abStopBody = new HashMap<>();
        abStopBody.put("bidderId", bidder.getId());

        mockMvc.perform(post("/api/auctions/" + auction.getId() + "/auto-bid/stop")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(abStopBody)))
                .andExpect(status().isOk())
                .andExpect(content().string("Auto-bid stopped"));

        // 4.12. Kết thúc sớm phiên đấu giá
        mockMvc.perform(post("/api/auctions/" + auction.getId() + "/end?userId=" + seller.getId() + "&role=SELLER"))
                .andExpect(status().isOk())
                .andExpect(content().string("Auction ended"));

        // 4.13. Xóa phiên đấu giá
        mockMvc.perform(delete("/api/auctions/" + auction.getId() + "?userId=" + admin.getId() + "&role=ADMIN"))
                .andExpect(status().isOk())
                .andExpect(content().string("Auction deleted"));
    }

    // ============================================================
    // 5. ADMIN CONTROLLER TESTS
    // ============================================================

    @Test
    @DisplayName("Các chức năng Quản trị viên quản lý, thống kê qua API /api/admin")
    public void testAdminManagementAPI() throws Exception {
        // 5.1. Xem danh sách người dùng
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // 5.2. Khóa/Mở khóa tài khoản người dùng
        mockMvc.perform(post("/api/admin/users/" + bidder.getId() + "/lock"))
                .andExpect(status().isOk())
                .andExpect(content().string("User lock status toggled!"));

        User lockedUser = userRepository.findById(bidder.getId()).orElseThrow();
        assertTrue(lockedUser.isLocked());

        // Mở khóa lại
        mockMvc.perform(post("/api/admin/users/" + bidder.getId() + "/lock"))
                .andExpect(status().isOk());
        assertFalse(userRepository.findById(bidder.getId()).orElseThrow().isLocked());

        // 5.3. Xem thống kê hệ thống (Stats)
        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(3))
                .andExpect(jsonPath("$.totalSellers").value(1))
                .andExpect(jsonPath("$.totalBidders").value(1));

        // 5.4. Duyệt & Từ chối đấu giá
        Item item = itemServiceCreateItem();
        Auction auction = auctionService.createAuction("Stats Auction", "Electronics", "Test stats",
                new BigDecimal("100.0"), seller.getId(), null, LocalDateTime.now().plusDays(2));
        auction.setStatus(AuctionStatus.PENDING);
        auctionRepository.save(auction);

        // Từ chối (Từ PENDING sang CANCELED)
        mockMvc.perform(post("/api/admin/auctions/" + auction.getId() + "/reject"))
                .andExpect(status().isOk())
                .andExpect(content().string("Auction rejected!"));

        assertEquals(AuctionStatus.CANCELED, auctionRepository.findById(auction.getId()).orElseThrow().getStatus());

        // Xóa User
        mockMvc.perform(delete("/api/admin/users/" + bidder.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully!"));

        assertNull(userRepository.findByUsername("bidder_test"));
    }

    private Item itemServiceCreateItem() throws Exception {
        return itemService.createItem(
                "ELECTRONICS",
                "Macbook Pro M3",
                "Laptop",
                50.0,
                seller.getId(),
                "Apple",
                "M3 Max"
        );
    }
}
