package com.nhom4project.auctionweb.client.dashboard;

<<<<<<< HEAD:PJB/auctionweb/src/test/java/com/nhom4project/auctionweb/client/dashboard/DashboardTest.java
<<<<<<< HEAD:PJB/auctionweb/src/test/java/com/nhom4project/auctionweb/client/dashboard/DashboardTest.java
import com.nhom4project.auctionweb.client.utils.WindowUtil;
=======
>>>>>>> main:auctionweb/src/test/java/com/nhom4project/auctionweb/client/dashboard/DashboardTest.java
=======
>>>>>>> f722d627f510dd91cb2323c2d79d99f63b52b9b8:auctionweb/src/test/java/com/nhom4project/auctionweb/client/dashboard/DashboardTest.java
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
<<<<<<< HEAD:PJB/auctionweb/src/test/java/com/nhom4project/auctionweb/client/dashboard/DashboardTest.java
<<<<<<< HEAD:PJB/auctionweb/src/test/java/com/nhom4project/auctionweb/client/dashboard/DashboardTest.java
 * Uses constructor mocking of Scene, and static mocking of both FXMLLoader and WindowUtil
=======
 * Uses constructor mocking of Scene, and static mocking of FXMLLoader
>>>>>>> main:auctionweb/src/test/java/com/nhom4project/auctionweb/client/dashboard/DashboardTest.java
=======
 * Uses constructor mocking of Scene, and static mocking of FXMLLoader
>>>>>>> f722d627f510dd91cb2323c2d79d99f63b52b9b8:auctionweb/src/test/java/com/nhom4project/auctionweb/client/dashboard/DashboardTest.java
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
<<<<<<< HEAD:PJB/auctionweb/src/test/java/com/nhom4project/auctionweb/client/dashboard/DashboardTest.java
<<<<<<< HEAD:PJB/auctionweb/src/test/java/com/nhom4project/auctionweb/client/dashboard/DashboardTest.java
             MockedStatic<WindowUtil> windowStatic = mockStatic(WindowUtil.class);
=======
             MockedStatic<com.nhom4project.auctionweb.client.utils.WindowUtil> windowUtilStatic = mockStatic(com.nhom4project.auctionweb.client.utils.WindowUtil.class);
>>>>>>> main:auctionweb/src/test/java/com/nhom4project/auctionweb/client/dashboard/DashboardTest.java
=======
             MockedStatic<com.nhom4project.auctionweb.client.utils.WindowUtil> windowUtilStatic = mockStatic(com.nhom4project.auctionweb.client.utils.WindowUtil.class);
>>>>>>> f722d627f510dd91cb2323c2d79d99f63b52b9b8:auctionweb/src/test/java/com/nhom4project/auctionweb/client/dashboard/DashboardTest.java
             MockedConstruction<Scene> mockScene = mockConstruction(Scene.class,
                     (mock, context) -> {
                         when(mock.getStylesheets()).thenReturn(mockStylesheets);
                     })) {

<<<<<<< HEAD:PJB/auctionweb/src/test/java/com/nhom4project/auctionweb/client/dashboard/DashboardTest.java
<<<<<<< HEAD:PJB/auctionweb/src/test/java/com/nhom4project/auctionweb/client/dashboard/DashboardTest.java
=======
=======
>>>>>>> f722d627f510dd91cb2323c2d79d99f63b52b9b8:auctionweb/src/test/java/com/nhom4project/auctionweb/client/dashboard/DashboardTest.java
            windowUtilStatic.when(() -> com.nhom4project.auctionweb.client.utils.WindowUtil.fitDashboard(any(Stage.class)))
                .thenAnswer(invocation -> {
                    Stage s = invocation.getArgument(0);
                    s.setMinWidth(980.0);
                    s.setMinHeight(680.0);
                    return null;
                });

<<<<<<< HEAD:PJB/auctionweb/src/test/java/com/nhom4project/auctionweb/client/dashboard/DashboardTest.java
>>>>>>> main:auctionweb/src/test/java/com/nhom4project/auctionweb/client/dashboard/DashboardTest.java
=======
>>>>>>> f722d627f510dd91cb2323c2d79d99f63b52b9b8:auctionweb/src/test/java/com/nhom4project/auctionweb/client/dashboard/DashboardTest.java
            // Intercept static FXML resource loading to return mock parent
            fxmlStatic.when(() -> FXMLLoader.load(any(URL.class))).thenReturn(mockParent);

            assertDoesNotThrow(() -> dashboard.start(mockStage));

            verify(mockStage).setTitle("Auction Web - Dashboard");
            verify(mockStage).setScene(any(Scene.class));
            verify(mockStage).show();
            verify(mockStylesheets).add(contains("dashboard.css"));

<<<<<<< HEAD:PJB/auctionweb/src/test/java/com/nhom4project/auctionweb/client/dashboard/DashboardTest.java
<<<<<<< HEAD:PJB/auctionweb/src/test/java/com/nhom4project/auctionweb/client/dashboard/DashboardTest.java
            // Verify window fits dashboard
            windowStatic.verify(() -> WindowUtil.fitDashboard(mockStage));
=======
            // Verify window fits dashboard constraints
            verify(mockStage).setMinWidth(980.0);
            verify(mockStage).setMinHeight(680.0);
>>>>>>> main:auctionweb/src/test/java/com/nhom4project/auctionweb/client/dashboard/DashboardTest.java
=======
            // Verify window fits dashboard constraints
            verify(mockStage).setMinWidth(980.0);
            verify(mockStage).setMinHeight(680.0);
>>>>>>> f722d627f510dd91cb2323c2d79d99f63b52b9b8:auctionweb/src/test/java/com/nhom4project/auctionweb/client/dashboard/DashboardTest.java
        }
    }
}
