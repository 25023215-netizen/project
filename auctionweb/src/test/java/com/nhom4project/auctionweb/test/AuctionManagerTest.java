package com.nhom4project.auctionweb.test;

import com.nhom4project.auctionweb.server.model.Auction;
import com.nhom4project.auctionweb.server.model.AuctionStatus;
import com.nhom4project.auctionweb.server.service.AuctionManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test cho AuctionManager - Nhóm 4.
 */
public class AuctionManagerTest {

    private AuctionManager manager;

    @BeforeEach
    public void setUp() {
        manager = AuctionManager.getInstance();
    }

    // ============================================================
    // PHẦN 1: KIỂM TRA CHỨC NĂNG (HAPPY PATH)
    // ============================================================

    @Test
    @DisplayName("Đăng ký và lấy trạng thái Auction thành công")
    public void testRegisterAndGetAuction_Success() {
        Auction auction = new Auction();
        auction.setId(999L);
        auction.setStatus(AuctionStatus.RUNNING);

        manager.registerAuction(auction);
        assertNotNull(manager.getAuction(999L));
        assertEquals(AuctionStatus.RUNNING, manager.getAuction(999L).getStatus());
    }

    // ============================================================
    // PHẦN 2: KIỂM TRA CÁC LỖI (BUGS & EDGE CASES)
    // ============================================================

    @Test
    @DisplayName("Lỗi Bug: Truy cập ID không tồn tại")
    public void testGetNonExistentAuction_Failure() {
        assertNull(manager.getAuction(8888L));
    }

    @Test
    @DisplayName("Lỗi Bug: Xử lý ID null an toàn")
    public void testRegisterNullId_Failure() {
        Auction auction = new Auction();
        auction.setId(null);
        assertDoesNotThrow(() -> manager.registerAuction(auction));
    }
}
