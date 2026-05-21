package com.nhom4project.auctionweb.client.app;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the App class using Equivalence Partitioning (EP) and isolation principles.
 * Uses Mockito's mockConstruction to stub dynamic instantiation of FXMLLoader and Scene,
 * ensuring complete isolation from the native JavaFX toolkit initialization.
 */
public class AppTest {

    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;

    @BeforeEach
    public void setUp() {
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void tearDown() {
        System.setErr(originalErr);
    }

    /**
     * TC_APP_01: Happy path execution.
     * Verifies that when start is called, FXMLLoader and Scene are constructed,
     * stylesheets are successfully loaded, and all expected Stage API methods are called.
     */
    @Test
    public void testStartHappyPath() {
        App app = new App();
        Stage mockStage = mock(Stage.class);
        Parent mockParent = mock(Parent.class);
        ObservableList<String> mockStylesheets = mock(ObservableList.class);

        try (MockedConstruction<FXMLLoader> mockLoader = mockConstruction(FXMLLoader.class,
                (mock, context) -> {
                    when(mock.load()).thenReturn(mockParent);
                });
             MockedConstruction<Scene> mockScene = mockConstruction(Scene.class,
                     (mock, context) -> {
                         when(mock.getStylesheets()).thenReturn(mockStylesheets);
                     })) {

            assertDoesNotThrow(() -> app.start(mockStage));

            // Verify Stage API calls
            verify(mockStage).setTitle("Online Auction System - Sign In");
            verify(mockStage).setScene(any(Scene.class));
            verify(mockStage).show();

            // Verify that the stylesheet was registered correctly
            verify(mockStylesheets).add(contains("signin.css"));
        }
    }

    /**
     * TC_APP_02: Robustness and Try-Catch error handling path.
     * Verifies that if an exception is thrown inside the start method (e.g. stage configuration fails),
     * the error is caught gracefully, does not propagate, and prints the expected critical error logs.
     */
    @Test
    public void testStartExceptionHandling() {
        App app = new App();
        Stage mockStage = mock(Stage.class);

        // Force an exception when Stage setTitle is called
        doThrow(new RuntimeException("Simulated Stage Error")).when(mockStage).setTitle(anyString());

        Parent mockParent = mock(Parent.class);
        ObservableList<String> mockStylesheets = mock(ObservableList.class);

        try (MockedConstruction<FXMLLoader> mockLoader = mockConstruction(FXMLLoader.class,
                (mock, context) -> {
                    when(mock.load()).thenReturn(mockParent);
                });
             MockedConstruction<Scene> mockScene = mockConstruction(Scene.class,
                     (mock, context) -> {
                         when(mock.getStylesheets()).thenReturn(mockStylesheets);
                     })) {

            // The exception must be swallowed internally and must not crash the run
            assertDoesNotThrow(() -> app.start(mockStage));

            // Assert that critical error message was logged
            String errOutput = errContent.toString();
            assertTrue(errOutput.contains("Critical Error starting application"));
            assertTrue(errOutput.contains("Simulated Stage Error"));
        }
    }
}
