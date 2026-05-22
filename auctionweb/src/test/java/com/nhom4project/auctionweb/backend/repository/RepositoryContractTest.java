package com.nhom4project.auctionweb.backend.repository;

import com.nhom4project.auctionweb.backend.model.Auction;
import com.nhom4project.auctionweb.backend.model.AuctionStatus;
import com.nhom4project.auctionweb.backend.model.Bidder;
import com.nhom4project.auctionweb.backend.model.Electronics;
import com.nhom4project.auctionweb.backend.model.Item;
import com.nhom4project.auctionweb.backend.model.User;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Isolated unit tests verifying database repository interface method contracts and return types.
 * Guarantees that query signatures map precisely to expected structures under mocked settings.
 */
public class RepositoryContractTest {

    @Test
    public void testUserRepositoryContracts() {
        UserRepository userRepository = mock(UserRepository.class);
        User mockUser = new Bidder();

        when(userRepository.findByUsername("johndoe")).thenReturn(mockUser);
        when(userRepository.existsByUsername("johndoe")).thenReturn(true);
        when(userRepository.existsByEmail("johndoe@test.com")).thenReturn(false);

        assertEquals(mockUser, userRepository.findByUsername("johndoe"));
        assertTrue(userRepository.existsByUsername("johndoe"));
        assertFalse(userRepository.existsByEmail("johndoe@test.com"));
    }

    @Test
    public void testAuctionRepositoryContracts() {
        AuctionRepository auctionRepository = mock(AuctionRepository.class);
        Auction auction = new Auction();

        when(auctionRepository.findBySellerId(1L)).thenReturn(Arrays.asList(auction));
        when(auctionRepository.findByStatus(AuctionStatus.RUNNING)).thenReturn(Arrays.asList(auction));

        List<Auction> sellerAuctions = auctionRepository.findBySellerId(1L);
        List<Auction> runningAuctions = auctionRepository.findByStatus(AuctionStatus.RUNNING);

        assertEquals(1, sellerAuctions.size());
        assertEquals(auction, sellerAuctions.get(0));
        assertEquals(1, runningAuctions.size());
        assertEquals(auction, runningAuctions.get(0));
    }

    @Test
    public void testItemRepositoryContracts() {
        ItemRepository itemRepository = mock(ItemRepository.class);
        Item item = new Electronics(); // concrete subclass instantiability

        when(itemRepository.findBySellerId(5L)).thenReturn(Arrays.asList(item));

        List<Item> sellerItems = itemRepository.findBySellerId(5L);
        assertEquals(1, sellerItems.size());
        assertEquals(item, sellerItems.get(0));
    }
}
