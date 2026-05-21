package com.nhom4project.auctionweb.client.signup;

<<<<<<< HEAD:PJB/auctionweb/src/test/java/com/nhom4project/auctionweb/client/signup/SignupTest.java
=======
import com.nhom4project.auctionweb.client.signup.Signup;
>>>>>>> main:auctionweb/src/test/java/com/nhom4project/auctionweb/client/signup/SignupTest.java
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
 * Unit tests for the Signup launcher view.
<<<<<<< HEAD:PJB/auctionweb/src/test/java/com/nhom4project/auctionweb/client/signup/SignupTest.java
 * Verifies stage title, scene configuration, and stylesheet injections under static mock isolation.
=======
 * Verifies stage title, scene configuration, and stylesheet injections under
 * static mock isolation.
>>>>>>> main:auctionweb/src/test/java/com/nhom4project/auctionweb/client/signup/SignupTest.java
 */
public class SignupTest {

    @Test
    public void testStart() {
        Signup signup = new Signup();
        Stage mockStage = mock(Stage.class);
        Parent mockParent = mock(Parent.class);
        ObservableList<String> mockStylesheets = mock(ObservableList.class);

        try (MockedStatic<FXMLLoader> fxmlStatic = mockStatic(FXMLLoader.class);
<<<<<<< HEAD:PJB/auctionweb/src/test/java/com/nhom4project/auctionweb/client/signup/SignupTest.java
             MockedConstruction<Scene> mockScene = mockConstruction(Scene.class,
                     (mock, context) -> {
                         when(mock.getStylesheets()).thenReturn(mockStylesheets);
                     })) {
=======
                MockedConstruction<Scene> mockScene = mockConstruction(Scene.class,
                        (mock, context) -> {
                            when(mock.getStylesheets()).thenReturn(mockStylesheets);
                        })) {
>>>>>>> main:auctionweb/src/test/java/com/nhom4project/auctionweb/client/signup/SignupTest.java

            // Mock static FXML loader
            fxmlStatic.when(() -> FXMLLoader.load(any(URL.class))).thenReturn(mockParent);

            assertDoesNotThrow(() -> signup.start(mockStage));

            verify(mockStage).setTitle("Đăng ký người dùng");
            verify(mockStage).setScene(any(Scene.class));
            verify(mockStage).show();
            verify(mockStylesheets).add(contains("signup.css"));
        }
    }
}
