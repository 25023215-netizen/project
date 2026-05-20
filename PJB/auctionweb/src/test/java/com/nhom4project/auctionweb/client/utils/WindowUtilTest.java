package com.nhom4project.auctionweb.client.utils;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WindowUtil.
 * Boots the JavaFX runtime in @BeforeAll to safely initialize JavaFX Screen subsystem,
 * then uses Mockito static mocking to verify sizing calculations.
 */
public class WindowUtilTest {

    @BeforeAll
    public static void initJFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Already initialized in another test
        }
    }

    /**
     * TC_WU_01: Fitting on a large display (Nominal/Standard Case).
     * Screen: 1920x1080
     */
    @Test
    public void testFitDashboardLargeScreen() {
        try (MockedStatic<Screen> screenMock = Mockito.mockStatic(Screen.class)) {
            Screen mockScreen = mock(Screen.class);
            screenMock.when(Screen::getPrimary).thenReturn(mockScreen);

            Rectangle2D bounds = new Rectangle2D(0, 0, 1920, 1080);
            when(mockScreen.getVisualBounds()).thenReturn(bounds);

            Stage mockStage = mock(Stage.class);

            assertDoesNotThrow(() -> WindowUtil.fitDashboard(mockStage));

            verify(mockStage).setWidth(1180.0);
            verify(mockStage).setHeight(760.0);
            verify(mockStage).setMinWidth(900.0);
            verify(mockStage).setMinHeight(640.0);
            verify(mockStage).setX(370.0);
            verify(mockStage).setY(160.0);
        }
    }

    /**
     * TC_WU_02: Fitting on a small display (Boundary Case).
     * Screen: 800x600
     */
    @Test
    public void testFitDashboardSmallScreen() {
        try (MockedStatic<Screen> screenMock = Mockito.mockStatic(Screen.class)) {
            Screen mockScreen = mock(Screen.class);
            screenMock.when(Screen::getPrimary).thenReturn(mockScreen);

            Rectangle2D bounds = new Rectangle2D(0, 0, 800, 600);
            when(mockScreen.getVisualBounds()).thenReturn(bounds);

            Stage mockStage = mock(Stage.class);

            assertDoesNotThrow(() -> WindowUtil.fitDashboard(mockStage));

            verify(mockStage).setWidth(800.0);
            verify(mockStage).setHeight(600.0);
            verify(mockStage).setMinWidth(800.0);
            verify(mockStage).setMinHeight(600.0);
            verify(mockStage).setX(0.0);
            verify(mockStage).setY(0.0);
        }
    }
}
