package com.nhom4project.auctionweb.frontend.controller;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Headless unit tests for SignupController.
 * Covers all boundary checks and equivalence partitions for local registration validation logic.
 */
public class SignupControllerTest {

    private SignupController controller;
    private TextField fullNameField;
    private TextField emailField;
    private TextField userNameField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private Button signUpButton;
    private Label statusLabel;
    private ComboBox<String> roleComboBox;

    @BeforeAll
    public static void initJFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Already initialized
        }
    }

    @BeforeEach
    public void setUp() throws Exception {
        controller = new SignupController();

        fullNameField = new TextField();
        emailField = new TextField();
        userNameField = new TextField();
        passwordField = new PasswordField();
        confirmPasswordField = new PasswordField();
        signUpButton = new Button();
        statusLabel = new Label();
        roleComboBox = new ComboBox<>();

        setField(controller, "fullNameField", fullNameField);
        setField(controller, "emailField", emailField);
        setField(controller, "userNameField", userNameField);
        setField(controller, "passwordField", passwordField);
        setField(controller, "confirmPasswordField", confirmPasswordField);
        setField(controller, "signUpButton", signUpButton);
        setField(controller, "statusLabel", statusLabel);
        setField(controller, "roleComboBox", roleComboBox);
    }

    @Test
    public void testButtonState_AllEmpty() {
        controller.initialize();

        assertTrue(signUpButton.isDisable());
        assertEquals("", statusLabel.getText());
    }

    @Test
    public void testButtonState_InvalidEmail() {
        controller.initialize();

        fullNameField.setText("Nguyen Van A");
        emailField.setText("invalid-email");
        userNameField.setText("user123");
        passwordField.setText("pass12345");
        confirmPasswordField.setText("pass12345");

        assertTrue(signUpButton.isDisable());
        assertEquals("Invalid email", statusLabel.getText());
    }

    @Test
    public void testButtonState_ShortUsername() {
        controller.initialize();

        fullNameField.setText("Nguyen Van A");
        emailField.setText("test@example.com");
        userNameField.setText("abc"); // too short (BVA boundary min- < 4)
        passwordField.setText("pass12345");
        confirmPasswordField.setText("pass12345");

        assertTrue(signUpButton.isDisable());
        assertEquals("Username must be at least 4 characters", statusLabel.getText());
    }

    @Test
    public void testButtonState_ShortPassword() {
        controller.initialize();

        fullNameField.setText("Nguyen Van A");
        emailField.setText("test@example.com");
        userNameField.setText("abcd"); // boundary min = 4
        passwordField.setText("pass"); // too short (BVA boundary min- < 8)
        confirmPasswordField.setText("pass");

        assertTrue(signUpButton.isDisable());
        assertEquals("Password must be at least 8 characters", statusLabel.getText());
    }

    @Test
    public void testButtonState_MismatchedPassword() {
        controller.initialize();

        fullNameField.setText("Nguyen Van A");
        emailField.setText("test@example.com");
        userNameField.setText("abcd");
        passwordField.setText("password123");
        confirmPasswordField.setText("password456"); // mismatch

        assertTrue(signUpButton.isDisable());
        assertEquals("Passwords do not match", statusLabel.getText());
    }

    @Test
    public void testButtonState_Success() {
        controller.initialize();

        fullNameField.setText("Nguyen Van A");
        emailField.setText("test@example.com");
        userNameField.setText("abcd");
        passwordField.setText("password123");
        confirmPasswordField.setText("password123");

        assertFalse(signUpButton.isDisable());
        assertEquals("", statusLabel.getText());
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
