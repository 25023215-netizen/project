package com.nhom4project.auctionweb.client.dashboard;

import com.nhom4project.auctionweb.client.utils.WindowUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the Dashboard launcher view.
 * Uses constructor mocking of Scene, and static mocking of both FXMLLoader and WindowUtil
 * to isolate graphics centering layout math during bootstrapping.
 */
public class DashboardTest {

    @Test
    public void testStart() {
        Dashboard dashboard = new Dashboard();
        Stage mockStage = mock(Stage.class);
        Parent mockParent = mock(Parent.class);
        ObservableList<String> mockStylesheets = mock(ObservableList.class);

        try (MockedStatic<FXMLLoader> fxmlStatic = mockStatic(FXMLLoader.class);
             MockedStatic<WindowUtil> windowStatic = mockStatic(WindowUtil.class);
             MockedConstruction<Scene> mockScene = mockConstruction(Scene.class,
                     (mock, context) -> {
                         when(mock.getStylesheets()).thenReturn(mockStylesheets);
                     })) {

            // Intercept static FXML resource loading to return mock parent
            fxmlStatic.when(() -> FXMLLoader.load(any(URL.class))).thenReturn(mockParent);

            assertDoesNotThrow(() -> dashboard.start(mockStage));

            verify(mockStage).setTitle("Auction Web - Dashboard");
            verify(mockStage).setScene(any(Scene.class));
            verify(mockStage).show();
            verify(mockStylesheets).add(contains("dashboard.css"));

            // Verify window fits dashboard
            windowStatic.verify(() -> WindowUtil.fitDashboard(mockStage));
        }
    }
}
