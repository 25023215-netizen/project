package com.nhom4project.auctionweb.backend.controller;

import com.nhom4project.auctionweb.backend.model.*;
import com.nhom4project.auctionweb.backend.repository.AuctionRepository;
import com.nhom4project.auctionweb.backend.repository.BidRepository;
import com.nhom4project.auctionweb.backend.service.AuctionService;
import com.nhom4project.auctionweb.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Isolated unit tests for AdminController.
 * Ensures complete service independence by mocking all data access layers and calculating edge cases.
 */
public class AdminControllerUnitTest {

    @Mock
    private UserService userService;

    @Mock
    private AuctionService auctionService;

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private BidRepository bidRepository;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllUsers_Success() {
        List<User> users = Arrays.asList(new Bidder(), new Seller());
        when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<?> response = adminController.getAllUsers();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
    }

    @Test
    public void testDeleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<?> response = adminController.deleteUser(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User deleted successfully!", response.getBody());
    }

    @Test
    public void testDeleteUser_Failure() throws Exception {
        doThrow(new RuntimeException("User not found!")).when(userService).deleteUser(99L);

        ResponseEntity<?> response = adminController.deleteUser(99L);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User not found!", response.getBody());
    }

    @Test
    public void testToggleLockUser_Success() throws Exception {
        doNothing().when(userService).toggleLockUser(1L);

        ResponseEntity<?> response = adminController.toggleLockUser(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User lock status toggled!", response.getBody());
    }

    @Test
    public void testToggleLockUser_Failure() throws Exception {
        doThrow(new RuntimeException("Error toggling status")).when(userService).toggleLockUser(1L);

        ResponseEntity<?> response = adminController.toggleLockUser(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error toggling status", response.getBody());
    }

    @Test
    public void testApproveAuction_Success() throws Exception {
        Auction auction = new Auction();
        auction.setId(10L);
        auction.setStatus(AuctionStatus.PENDING);

        when(auctionService.getAuctionById(10L)).thenReturn(Optional.of(auction));
        when(auctionRepository.save(any(Auction.class))).thenReturn(auction);

        ResponseEntity<?> response = adminController.approveAuction(10L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Auction approved!", response.getBody());
        assertEquals(AuctionStatus.OPEN, auction.getStatus());
        verify(auctionRepository, times(1)).save(auction);
    }

    @Test
    public void testApproveAuction_Failure_NotFound() {
        when(auctionService.getAuctionById(10L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = adminController.approveAuction(10L);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Auction not found!", response.getBody());
    }

    @Test
    public void testApproveAuction_Failure_NotPending() {
        Auction auction = new Auction();
        auction.setStatus(AuctionStatus.RUNNING);

        when(auctionService.getAuctionById(10L)).thenReturn(Optional.of(auction));

        ResponseEntity<?> response = adminController.approveAuction(10L);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Auction is not in PENDING status!", response.getBody());
    }

    @Test
    public void testRejectAuction_Success() throws Exception {
        Auction auction = new Auction();
        auction.setStatus(AuctionStatus.PENDING);

        when(auctionService.getAuctionById(10L)).thenReturn(Optional.of(auction));
        when(auctionRepository.save(any(Auction.class))).thenReturn(auction);

        ResponseEntity<?> response = adminController.rejectAuction(10L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Auction rejected!", response.getBody());
        assertEquals(AuctionStatus.CANCELED, auction.getStatus());
        verify(auctionRepository, times(1)).save(auction);
    }

    @Test
    public void testGetStats_Success() {
        User u1 = new Seller();
        User u2 = new Bidder();
        User u3 = new Admin();
        List<User> users = Arrays.asList(u1, u2, u3);

        Auction a1 = new Auction();
        a1.setStatus(AuctionStatus.RUNNING);
        a1.setCurrentPrice(new BigDecimal("100.00"));

        Auction a2 = new Auction();
        a2.setStatus(AuctionStatus.FINISHED);
        a2.setCurrentPrice(new BigDecimal("250.00"));

        List<Auction> auctions = Arrays.asList(a1, a2);

        when(userService.getAllUsers()).thenReturn(users);
        when(auctionService.listAuctions()).thenReturn(auctions);
        when(bidRepository.count()).thenReturn(15L);

        ResponseEntity<?> response = adminController.getStats();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> stats = (Map<String, Object>) response.getBody();
        assertNotNull(stats);
        assertEquals(3, stats.get("totalUsers"));
        assertEquals(1L, stats.get("totalSellers"));
        assertEquals(1L, stats.get("totalBidders"));
        assertEquals(1L, stats.get("totalAdmins"));
        assertEquals(2, stats.get("totalAuctions"));
        assertEquals(1L, stats.get("runningAuctions"));
        assertEquals(1L, stats.get("finishedAuctions"));
        assertEquals(new BigDecimal("250.00"), stats.get("totalRevenue"));
        assertEquals(new BigDecimal("250.00"), stats.get("highestBid"));
        assertEquals(15L, stats.get("totalBidTransactions"));
    }
}
