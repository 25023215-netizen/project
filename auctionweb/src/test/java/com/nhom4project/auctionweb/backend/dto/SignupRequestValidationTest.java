package com.nhom4project.auctionweb.backend.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Isolated unit tests validating field constraints on the SignupRequest DTO.
 * Uses the real Hibernate Validator engine to verify Equivalence Partitioning (EP) and Boundary Value Analysis (BVA).
 */
public class SignupRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void testSignupRequest_ValidData() {
        SignupRequest req = new SignupRequest();
        req.setFullname("John Doe");
        req.setEmail("johndoe@test.com");
        req.setUsername("johndoe");
        req.setPassword("password123");
        req.setRole("BIDDER");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(req);
        assertTrue(violations.isEmpty(), "Valid request should produce zero constraint violations");
    }

    @Test
    public void testSignupRequest_BlankFullname() {
        SignupRequest req = new SignupRequest();
        req.setFullname(" "); // blank
        req.setEmail("johndoe@test.com");
        req.setUsername("johndoe");
        req.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Full name is required")));
    }

    @Test
    public void testSignupRequest_InvalidEmailFormat() {
        SignupRequest req = new SignupRequest();
        req.setFullname("John Doe");
        req.setEmail("invalid-email-address"); // lacks '@'
        req.setUsername("johndoe");
        req.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email is invalid")));
    }

    @Test
    public void testSignupRequest_UsernameTooShort() {
        SignupRequest req = new SignupRequest();
        req.setFullname("John Doe");
        req.setEmail("johndoe@test.com");
        req.setUsername("abc"); // too short (BVA boundary min- < 4)
        req.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Username must have at least 4 characters")));
    }

    @Test
    public void testSignupRequest_PasswordTooShort() {
        SignupRequest req = new SignupRequest();
        req.setFullname("John Doe");
        req.setEmail("johndoe@test.com");
        req.setUsername("johndoe");
        req.setPassword("short"); // too short (BVA boundary min- < 8)

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password must have at least 8 characters")));
    }
}
