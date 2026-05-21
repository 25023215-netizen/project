package com.nhom4project.auctionweb.frontend.controller;

import com.nhom4project.auctionweb.frontend.utils.BackendClient;
import com.nhom4project.auctionweb.frontend.utils.SessionManager;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Headless unit tests for SigninController.
 * Boots JavaFX platform once for component instantiations, and injects dependencies via reflection.
 */
public class SigninControllerTest {

    private SigninController controller;
    private TextField userNameField;
    private PasswordField passwordField;
    private Button signinButton;
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
        controller = new SigninController();

        userNameField = new TextField();
        passwordField = new PasswordField();
        signinButton = new Button();
        statusLabel = new Label();
        roleComboBox = new ComboBox<>();

        // Inject FXML fields via reflection
        setField(controller, "userNameField", userNameField);
        setField(controller, "passwordField", passwordField);
        setField(controller, "signinButton", signinButton);
        setField(controller, "statusLabel", statusLabel);
        setField(controller, "roleComboBox", roleComboBox);
    }

    @Test
    public void testButtonStateLifecycle() {
        // Initializing registers text listeners
        controller.initialize();

        // 1. Both Empty -> Should be disabled
        assertTrue(signinButton.isDisable());
        assertEquals("", statusLabel.getText());

        // 2. Username entered, Password empty -> Should be disabled
        userNameField.setText("admin");
        assertTrue(signinButton.isDisable());

        // 3. Both entered -> Should be enabled!
        passwordField.setText("pass12345");
        assertFalse(signinButton.isDisable());

        // 4. Back to empty -> Should be disabled
        userNameField.setText(" ");
        assertTrue(signinButton.isDisable());
    }

    @Test
    public void testEscapeJson() throws Exception {
        java.lang.reflect.Method escapeMethod = SigninController.class.getDeclaredMethod("escapeJson", String.class);
        escapeMethod.setAccessible(true);

        String result = (String) escapeMethod.invoke(controller, "user\"name\\");
        assertEquals("user\\\"name\\\\", result);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
