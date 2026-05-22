package com.nhom4project.auctionweb.backend.controller;

import com.nhom4project.auctionweb.backend.dto.SigninRequest;
import com.nhom4project.auctionweb.backend.dto.SignupRequest;
import com.nhom4project.auctionweb.backend.model.Bidder;
import com.nhom4project.auctionweb.backend.model.Roles;
import com.nhom4project.auctionweb.backend.model.User;
import com.nhom4project.auctionweb.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Isolated unit tests for AuthController using pure Mockito unit mocks.
 * Bypasses full Spring context starting to guarantee < 10ms execution.
 */
public class AuthControllerUnitTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setUsername("test_user");
        request.setPassword("password123");
        request.setEmail("test@gmail.com");
        request.setFullname("Test User");
        request.setRole("BIDDER");

        doNothing().when(userService).registerUser(any(SignupRequest.class));

        ResponseEntity<?> response = authController.registerUser(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully!", response.getBody());
        verify(userService, times(1)).registerUser(request);
    }

    @Test
    public void testRegisterUser_Failure_DuplicateUser() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setUsername("existing_user");

        doThrow(new RuntimeException("Username already exists!")).when(userService).registerUser(any(SignupRequest.class));

        ResponseEntity<?> response = authController.registerUser(request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username already exists!", response.getBody());
    }

    @Test
    public void testAuthenticateUser_Success() throws Exception {
        SigninRequest request = new SigninRequest();
        request.setUsername("bidder_user");
        request.setPassword("pass123");

        User mockUser = new Bidder();
        mockUser.setUsername("bidder_user");
        mockUser.setRole(Roles.BIDDER);

        when(userService.authenticate("bidder_user", "pass123")).thenReturn(mockUser);

        ResponseEntity<?> response = authController.authenticateUser(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUser, response.getBody());
        verify(userService, times(1)).authenticate("bidder_user", "pass123");
    }

    @Test
    public void testAuthenticateUser_Failure_WrongPassword() throws Exception {
        SigninRequest request = new SigninRequest();
        request.setUsername("bidder_user");
        request.setPassword("wrong_pass");

        when(userService.authenticate("bidder_user", "wrong_pass"))
                .thenThrow(new RuntimeException("Invalid credentials!"));

        ResponseEntity<?> response = authController.authenticateUser(request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid credentials!", response.getBody());
    }
}
