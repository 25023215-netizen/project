package com.nhom4project.auctionweb.backend.service;

import com.nhom4project.auctionweb.backend.model.Art;
import com.nhom4project.auctionweb.backend.model.Electronics;
import com.nhom4project.auctionweb.backend.model.Item;
import com.nhom4project.auctionweb.backend.model.Vehicle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test cho ItemFactory - Nhóm 4.
 */
public class ItemFactoryTest {

    // ============================================================
    // PHẦN 1: KIỂM TRA CÁC TRƯỜNG HỢP THÀNH CÔNG (HAPPY PATH)
    // ============================================================

    @Test
    @DisplayName("Test tạo Electronics thành công")
    public void testCreateElectronics_Success() {
        Item item = ItemFactory.createItem("ELECTRONICS");
        assertNotNull(item);
        assertTrue(item instanceof Electronics);
    }

    @Test
    @DisplayName("Test tạo Art thành công")
    public void testCreateArt_Success() {
        Item item = ItemFactory.createItem("ART");
        assertNotNull(item);
        assertTrue(item instanceof Art);
    }

    @Test
    @DisplayName("Test tạo Vehicle thành công")
    public void testCreateVehicle_Success() {
        Item item = ItemFactory.createItem("VEHICLE");
        assertNotNull(item);
        assertTrue(item instanceof Vehicle);
    }

    @ParameterizedTest
    @ValueSource(strings = {"electronics", "Electronics", "eLeCtRoNiCs"})
    @DisplayName("Test tính không phân biệt chữ hoa chữ thường")
    public void testCaseInsensitivity_Success(String type) {
        Item item = ItemFactory.createItem(type);
        assertNotNull(item);
        assertTrue(item instanceof Electronics);
    }

    // ============================================================
    // PHẦN 2: KIỂM TRA CÁC LỖI (BUGS & EDGE CASES)
    // ============================================================

    @Test
    @DisplayName("Lỗi Bug: Loại sản phẩm không tồn tại")
    public void testUnknownType_Failure() {
        assertThrows(IllegalArgumentException.class, () -> ItemFactory.createItem("FOOD"));
    }

    @Test
    @DisplayName("Lỗi Bug: Giá trị null")
    public void testNullType_Failure() {
        assertThrows(IllegalArgumentException.class, () -> ItemFactory.createItem(null));
    }

    @Test
    @DisplayName("Kiểm tra private/implicit constructor bằng reflection")
    public void testConstructor() throws Exception {
        java.lang.reflect.Constructor<ItemFactory> constructor = ItemFactory.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        ItemFactory instance = constructor.newInstance();
        assertNotNull(instance);
    }
}
