package com.nhom4project.auctionweb.frontend.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SessionManager.
 * Uses Equivalence Partitioning (EP) and Boundary Value Analysis (BVA) for inputs and state.
 */
public class SessionManagerTest {

    private SessionManager sessionManager;

    @BeforeEach
    public void setUp() {
        sessionManager = SessionManager.getInstance();
        sessionManager.clear();
    }

    /**
     * TC_SM_01: Singleton pattern integrity.
     */
    @Test
    public void testGetInstance() {
        SessionManager instance1 = SessionManager.getInstance();
        SessionManager instance2 = SessionManager.getInstance();
        assertNotNull(instance1);
        assertSame(instance1, instance2);
    }

    /**
     * TC_SM_02: Happy path log in and log out lifecycle.
     */
    @Test
    public void testSetUserAndClear() {
        assertFalse(sessionManager.isLoggedIn());
        assertNull(sessionManager.getUserId());
        assertNull(sessionManager.getUsername());
        assertNull(sessionManager.getFullname());
        assertNull(sessionManager.getRole());

        // Login user
        sessionManager.setUser(1L, "john_doe", "John Doe", "BIDDER");

        assertTrue(sessionManager.isLoggedIn());
        assertEquals(1L, sessionManager.getUserId());
        assertEquals("john_doe", sessionManager.getUsername());
        assertEquals("John Doe", sessionManager.getFullname());
        assertEquals("BIDDER", sessionManager.getRole());

        // Clear session
        sessionManager.clear();
        assertFalse(sessionManager.isLoggedIn());
        assertNull(sessionManager.getUserId());
        assertNull(sessionManager.getUsername());
        assertNull(sessionManager.getFullname());
        assertNull(sessionManager.getRole());
    }

    /**
     * TC_SM_03: Case insensitivity verification on user roles.
     */
    @Test
    public void testRolesCaseInsensitive() {
        // Test BIDDER (mixed case)
        sessionManager.setUser(1L, "user1", "Name", "BiDdEr");
        assertTrue(sessionManager.isBidder());
        assertFalse(sessionManager.isSeller());
        assertFalse(sessionManager.isAdmin());

        // Test SELLER (mixed case)
        sessionManager.setUser(1L, "user1", "Name", "sElLeR");
        assertFalse(sessionManager.isBidder());
        assertTrue(sessionManager.isSeller());
        assertFalse(sessionManager.isAdmin());

        // Test ADMIN (mixed case)
        sessionManager.setUser(1L, "user1", "Name", "AdMiN");
        assertFalse(sessionManager.isBidder());
        assertFalse(sessionManager.isSeller());
        assertTrue(sessionManager.isAdmin());
    }

    /**
     * TC_SM_04: Handling null and invalid roles (Boundary values).
     */
    @Test
    public void testRoleNullAndInvalid() {
        // Null role input
        sessionManager.setUser(1L, "user1", "Name", null);
        assertFalse(sessionManager.isBidder());
        assertFalse(sessionManager.isSeller());
        assertFalse(sessionManager.isAdmin());

        // Unrecognized/Invalid role input
        sessionManager.setUser(1L, "user1", "Name", "GUEST");
        assertFalse(sessionManager.isBidder());
        assertFalse(sessionManager.isSeller());
        assertFalse(sessionManager.isAdmin());
    }
}
